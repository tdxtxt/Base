package com.baselib.helper.image

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
    fun loadImage(view: ImageView?, url: String)
    fun loadImage(view: ImageView?, url: String, @DrawableRes placeholderResId: Int)
    fun loadImage(view: ImageView?, url: String, @DrawableRes placeholderResId: Int, isCache: Boolean)
    fun saveImage(url: String, destFile: File, callback: (isSuccess: Boolean, msg: String) -> Unit)

    fun clearMemoryCache()
    fun clearDiskCache()
}