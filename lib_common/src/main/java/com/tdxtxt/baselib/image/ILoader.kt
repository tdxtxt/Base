package com.tdxtxt.baselib.image

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import java.io.File

/**
 * 创建时间： 2020/5/25
 * 编码： tangdex
 * 功能描述:
 */
interface ILoader {
    fun init(){}
    fun loadGif(view: ImageView?, @DrawableRes resId: Int, isPlay: Boolean)
    fun loadImage(view: ImageView?, @DrawableRes resId: Int)
    fun loadImage(view: ImageView?, url: String?)
    fun loadImage(view: ImageView?, url: String?, isCache: Boolean)
    fun loadImage(view: ImageView?, url: String?, @DrawableRes placeholderResId: Int)
    fun loadImage(view: ImageView?, url: String?, @DrawableRes placeholderResId: Int, isCache: Boolean, radiusdp: Float = 0f)
    fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float)
    fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float, @DrawableRes placeholderResId: Int)
    fun loadImageCircle(view: ImageView?, url: String?)
    fun loadImageCircle(view: ImageView?, url: String?, @DrawableRes placeholderResId: Int, centerCrop: Boolean = true)
    fun loadImageCircle(view: ImageView?, url: String?, borderWidthDp: Float, @ColorInt borderColor: Int)
    fun loadImageCircle(view: ImageView?, url: String?, borderWidthDp: Float, @ColorInt borderColor: Int, @DrawableRes placeholderResId: Int)
    fun downloadImage(context: Context?, url: String?, callback: (file: File?) -> Unit)
    //同步获取
    fun downloadImageSync(context: Context?, url: String?): File?
    fun getBitmapSync(context: Context?, url: String?, width: Float? = null, height: Float? = null): Bitmap?
    fun getBitmapSync(view: View?, url: String?, width: Float? = null, height: Float? = null): Bitmap?

    fun clearMemoryCache()
    fun clearDiskCache()
}