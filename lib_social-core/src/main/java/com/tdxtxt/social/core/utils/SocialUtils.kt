package com.tdxtxt.social.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
object SocialUtils {
    private const val THUMB_IMAGE_SIZE = 32 * 1024
    private const val POINT_GIF = ".gif"
    private const val POINT_JPG = ".jpg"
    private const val POINT_JPEG = ".jpeg"
    private const val POINT_PNG = ".png"

    /**
     * @param path 路径
     * @return 是不是 png 文件
     */
    fun isPngFile(path: String): Boolean {
        return path.toLowerCase().endsWith(POINT_PNG)
    }

    /**
     * @param path 路径
     * @return 是否是 gif 文件
     */
    fun isGifFile(path: String): Boolean {
        return path.toLowerCase().endsWith(POINT_GIF)
    }

    /**
     * @param path 路径
     * @return 是不是 jpg || png
     */
    private fun isJpgPngFile(path: String): Boolean {
        return isJpgFile(path) || isPngFile(path)
    }

    /**
     * @param path 路径
     * @return 是不是 jpg 文件
     */
    private fun isJpgFile(path: String): Boolean {
        return path.toLowerCase().endsWith(POINT_JPG) || path.toLowerCase().endsWith(POINT_JPEG)
    }

    /**
     * @param path 路径
     * @return 是不是 图片 文件
     */
    fun isPicFile(path: String): Boolean {
        return isJpgPngFile(path) || isGifFile(path)
    }

    /**
     * 根据路径获取指定大小的图片
     * @param path    路径
     * @return byte[]
     */
    fun getStaticSizeBitmapByteByPath(path: String?): ByteArray? {
        if(path == null) return null
        val srcBitmap = getMaxSizeBitmap(path, THUMB_IMAGE_SIZE)
        var format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        if (isPngFile(path)) format = Bitmap.CompressFormat.PNG
        return getStaticSizeBitmapByteByBitmap(srcBitmap, THUMB_IMAGE_SIZE, format)
    }

    /**
     * 根据路径获取指定大小的图片
     * @param path    路径
     * @return byte[]
     */
    fun getStaticSizeBitmapByPath(path: String?): Bitmap? {
        if(path == null) return null
        return getMaxSizeBitmap(path, THUMB_IMAGE_SIZE)
    }

    /**
     * 使用 path decode 出来一个差不多大小的，此时因为图片质量的关系，可能大于kbNum
     *
     * @param filePath path
     * @param maxSize  byte
     * @return bitmap
     */
    private fun getMaxSizeBitmap(filePath: String, maxSize: Int): Bitmap {
        val originSize = getBitmapSize(filePath)
        Log.i("Social", "原始图片大小 = " + originSize.first + " * " + originSize.second)
        var sampleSize = 0
        // 我们对较小的图片不进行采样，因为采样只是尽量接近 32k 和避免占用大量内存
        // 对较小图片进行采样会导致图片更模糊，所以对不大的图片，直接走后面的细节调整
        if (originSize.first * originSize.first < 400 * 400) {
            sampleSize = 1
        } else {
            val size = calculateSize(originSize, maxSize * 5)
            Log.i("Social","目标图片大小 = " + size.first + " * " + size.second)
            while (sampleSize == 0
                || originSize.second / sampleSize > size.second
                || originSize.first / sampleSize > size.first) {
                sampleSize += 2
            }
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = sampleSize
        options.inMutable = true
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        Log.i("Social","sample size = " + sampleSize + " 采样后 bitmap大小 = " + bitmap.byteCount)
        return bitmap
    }

    /**
     * 获取图片大小
     *
     * @param filePath 路径
     * @return Size
     */
    private fun getBitmapSize(filePath: String): Pair<Int, Int> {
        // 仅获取宽高
        val options = BitmapFactory.Options()
        // 该属性设置为 true 只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        // 获得原图的宽和高
        val outWidth = options.outWidth
        val outHeight = options.outHeight
        return Pair(outWidth, outHeight)
    }

    /**
     * 根据kb计算缩放后的大约宽高
     *
     * @param originSize 图片原始宽高
     * @param maxSize    byte length
     * @return 大小
     */
    private fun calculateSize(originSize: Pair<Int, Int>, maxSize: Int): Pair<Int, Int> {
        val bw = originSize.first
        val bh = originSize.second
        // 如果本身已经小于，就直接返回
        if (bw * bh <= maxSize) {
            return Pair(bw, bh)
        }
        // 拿到大于1的宽高比
        var isHeightLong = true
        var bitRatio = bh * 1f / bw
        if (bitRatio < 1) {
            bitRatio = bw * 1f / bh
            isHeightLong = false
        }
        // 较长边 = 较短边 * 比例(>1)
        // maxSize = 较短边 * 较长边 = 较短边 * 较短边 * 比例(>1)
        // 由此计算短边应该为 较短边 = sqrt(maxSize/比例(>1))
        val thumbShort = Math.sqrt((maxSize / bitRatio).toDouble()).toInt()
        // 较长边 = 较短边 * 比例(>1)
        val thumbLong = (thumbShort * bitRatio).toInt()
        return if (isHeightLong) {
            Pair(thumbShort, thumbLong)
        } else {
            Pair(thumbLong, thumbShort)
        }
    }

    /**
     * 创建指定大小的bitmap的byte流，大小 <= maxSize
     *
     * @param srcBitmap bitmap
     * @param maxSize   kb,example 32kb
     * @return byte流
     */
    private fun getStaticSizeBitmapByteByBitmap(srcBitmap: Bitmap, maxSize: Int, format: Bitmap.CompressFormat): ByteArray? {
        var bitmap = srcBitmap
        // 首先进行一次大范围的压缩
        var tempBitmap: Bitmap
        val output = ByteArrayOutputStream()
        // 设置矩阵数据
        val matrix = Matrix()
        bitmap.compress(format, 100, output)
        // 如果进行了上面的压缩后，依旧大于32K，就进行小范围的微调压缩
        var bytes = output.toByteArray()
        Log.i("Social","开始循环压缩之前 bytes = " + bytes.size)
        while (bytes.size > maxSize) {
            matrix.setScale(0.9f, 0.9f)//每次缩小 1/10
            tempBitmap = bitmap
            bitmap = Bitmap.createBitmap(
                tempBitmap, 0, 0,
                tempBitmap.width, tempBitmap.height, matrix, true)
            recyclerBitmaps(tempBitmap)
            output.reset()
            bitmap.compress(format, 100, output)
            bytes = output.toByteArray()
            Log.i("Social","压缩一次 bytes = " + bytes.size)
        }
        Log.i("Social","压缩后的图片输出大小 bytes = " + bytes.size)
        recyclerBitmaps(bitmap)
        return bytes
    }

    private fun recyclerBitmaps(vararg bitmaps: Bitmap?) {
        try {
            for (bitmap in bitmaps) {
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 任何一个为空 返回true
    fun isAnyEmpty(vararg strings: String?): Boolean {
        var isEmpty = false
        for (string in strings) {
            if (TextUtils.isEmpty(string)) {
                isEmpty = true
                break
            }
        }
        return isEmpty
    }
}