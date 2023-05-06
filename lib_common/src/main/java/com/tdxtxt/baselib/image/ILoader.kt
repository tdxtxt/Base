package com.tdxtxt.baselib.image

import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.io.File

/**
 * 创建时间： 2020/5/25
 * 编码： tangdex
 * 功能描述:
 */
interface ILoader {
    fun init(){}
    fun toggleGif(view: ImageView?, @DrawableRes resId: Int, resume: Boolean)
    fun loadImage(view: ImageView?, @DrawableRes resId: Int)
    fun loadImage(view: ImageView?, url: String?)
    fun loadImage(view: ImageView?, url: String?, isCache: Boolean)
    fun loadImage(view: ImageView?, url: String?, @DrawableRes placeholderResId: Int)
    fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float)
    fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float, @DrawableRes placeholderResId: Int)
    fun loadImage(view: ImageView?, url: String?, @DrawableRes placeholderResId: Int, isCache: Boolean, radiusdp: Float = 0f)
    fun loadImageCircle(view: ImageView?, url: String?)
    fun loadImageCircle(view: ImageView?, url: String?, placeholderResId: Int)
    fun saveImage(url: String?, destFile: File, callback: (isSuccess: Boolean, msg: String) -> Unit)

    fun clearMemoryCache()
    fun clearDiskCache()
}