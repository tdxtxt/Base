package com.tdxtxt.social.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
object FileUtils {
    private const val SHARE_CACHE_DIR = "share"

    fun initCacheDir(context: Context, dirName: String): String {
        //storage/emulated/0/Android/data/{packageName}/cache
        val file = File(context.externalCacheDir, SHARE_CACHE_DIR)
        if(!isFileExist(file)) file.mkdirs()
        return file.absolutePath
    }

    fun isFileExist(file: File?): Boolean {
        if(file == null) return false
        return file.exists() && file.length() > 0
    }

    /**
     * 将资源图片映射到本地文件存储，同一张图片不必重复decode
     * @param context ctx
     * @param resId   资源ID
     * @return 路径
     */
    fun mapResId2LocalPath(context: Context?, resId: Int?): String? {
        if(context == null) return null
        if(resId == null) return null
        val fileName = String.format("thumb-%s.png", resId)
        val cacheDir = initCacheDir(context, SHARE_CACHE_DIR)
        val saveFile = File(cacheDir, fileName)
        if (saveFile.exists())
            return saveFile.absolutePath
        var bitmap: Bitmap? = null
        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                val drawable = if(resId == 0) getAppLogo(context) else context.getDrawable(resId)
                drawable?.apply {
                    bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                    bitmap?.apply {
                        val canvas = Canvas(this)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                    }
                }
            }else{
                bitmap = BitmapFactory.decodeResource(context.resources, resId)
            }
            if (bitmap != null && (bitmap?.width?:0) > 0 && (bitmap?.height?:0) > 0) {
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(saveFile))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } finally {
            recyclerBitmaps(bitmap)
        }
        return saveFile.absolutePath
    }

    private fun getAppLogo(context: Context): Drawable? {
        var drawble: Drawable? = null
        try{
            drawble = context.packageManager.getApplicationIcon(context.packageName)
        }catch (e: Exception){}
        return drawble
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
}