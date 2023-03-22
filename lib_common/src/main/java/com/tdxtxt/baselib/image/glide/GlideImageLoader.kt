package com.tdxtxt.baselib.image.glide

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.tdxtxt.baselib.image.ILoader
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifOptions
import com.bumptech.glide.request.RequestOptions
import com.tdxtxt.baselib.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

/**
 * 创建时间： 2020/5/25
 * 编码： tangdex
 * 功能描述:
 */
object GlideImageLoader : ILoader {

    override fun init() {

    }

    override fun toggleGif(view: ImageView?, resId: Int, resume: Boolean) {
        if (view == null) return
        if(isDestory(view.context)) return
        if (view.drawable is GifDrawable) {
            val drawable = view.drawable as GifDrawable
            if (resume) {
                if (!drawable.isRunning) drawable.start()
            } else {
                if (drawable.isRunning) drawable.stop()
            }
        } else {
            Glide.with(view.context).load(resId).set(GifOptions.DECODE_FORMAT, DecodeFormat.DEFAULT)
                .into(view)

            if (view.drawable is GifDrawable) {
                val drawable = view.drawable as GifDrawable
                if (resume) {
                    if (!drawable.isRunning) drawable.start()
                } else {
                    if (drawable.isRunning) drawable.stop()
                }
            }
        }
    }

    override fun loadImage(view: ImageView?, resId: Int) {
        if (view == null) return
        if(isDestory(view.context)) return
        Glide.with(view.context).load(resId).into(view)
    }

    override fun loadImage(view: ImageView?, url: String?) {
        loadImage(view, url, R.drawable.baselib_image_placeholder, true)
    }

    override fun loadImage(view: ImageView?, url: String?, placeholderResId: Int) {
        loadImage(view, url, placeholderResId, true)
    }

    override fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float) {
        loadImage(view, url, R.drawable.baselib_image_placeholder, true, radiusdp)
    }

    @SuppressLint("ResourceType")
    override fun loadImage(
        view: ImageView?,
        url: String?,
        placeholderResId: Int,
        isCache: Boolean,
        radiusdp: Float
    ) {
        if (view == null) return
        if(isDestory(view.context)) return
        if (TextUtils.isEmpty(url)) {
            if (placeholderResId > 0) view.setImageResource(placeholderResId)
            return
        }
        val requests = Glide.with(view.context)
        val request = requests.load(url)
        if(radiusdp > 0){
            val options = RequestOptions()
            options.transform(RoundedCornersTransformation(SizeUtils.dp2px(radiusdp), 0))
            request.apply(options)
        }
        request.transition(DrawableTransitionOptions().crossFade())
        when {
            placeholderResId > 0 -> {
                request.placeholder(placeholderResId)
                request.error(placeholderResId)
            }
            !isCache -> {
                // 跳过内存缓存
                request.skipMemoryCache(true)
                // 跳过磁盘缓存
                request.diskCacheStrategy(DiskCacheStrategy.NONE)
            }
        }
        request.into(view)
    }


    override fun saveImage(
        url: String?,
        destFile: File,
        callback: (isSuccess: Boolean, msg: String) -> Unit
    ) {
    }


//    override fun loadRoundRect(view: ImageView?, url: String?, radiusdp: Float) {
//        val options = RequestOptions()
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .transform(RoundedCornersTransformation(SizeUtils.dp2px(radiusdp), 0))
//
//        Glide.with(context).load(url).apply(options).into(imgView);
//    }


    override fun clearMemoryCache() {
    }

    override fun clearDiskCache() {
    }

    private fun isDestory(context: Context?): Boolean{
        if (context == null) return true
        if (context is Activity) {
            return context.isFinishing || context.isDestroyed
        }
        return false
    }

}