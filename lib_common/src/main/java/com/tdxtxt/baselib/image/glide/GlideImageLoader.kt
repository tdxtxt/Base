package com.tdxtxt.baselib.image.glide

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.tdxtxt.baselib.image.ILoader
import com.tdxtxt.baselib.image.ImageLoader
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
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

    override fun loadGif(view: ImageView?, resId: Int, isPlay: Boolean) {
        if (view == null) return
        if(isDestory(view.context)) return

        if (view.drawable is GifDrawable) {
            val drawable = view.drawable as GifDrawable
            if (isPlay) {
                if (!drawable.isRunning) drawable.start()
            } else {
                if (drawable.isRunning) drawable.stop()
            }
        } else {
            Glide.with(view.context).load(resId)
                .set(GifOptions.DECODE_FORMAT, DecodeFormat.DEFAULT)
                .into(view)

            if (view.drawable is GifDrawable) {
                val drawable = view.drawable as GifDrawable
                if (isPlay) {
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
        loadImage(view, url, ImageLoader.placeholderResId, true)
    }

    override fun loadImage(view: ImageView?, url: String?, isCache: Boolean) {
        loadImage(view, url, ImageLoader.placeholderResId, isCache)
    }

    override fun loadImage(view: ImageView?, url: String?, placeholderResId: Int) {
        loadImage(view, url, placeholderResId, true)
    }

    override fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float) {
        loadImageRoundRect(view, url, radiusdp, ImageLoader.placeholderResId)
    }

    override fun loadImageRoundRect(view: ImageView?, url: String?, radiusdp: Float, placeholderResId: Int) {
        loadImage(view, url, placeholderResId, true, radiusdp)
    }

    override fun loadImageCircle(view: ImageView?, url: String?) {
        loadImageCircle(view, url, ImageLoader.placeholderResId)
    }

    override fun loadImageCircle(view: ImageView?, url: String?, placeholderResId: Int) {
        if (view == null) return
        if(isDestory(view.context)) return

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(placeholderResId)
            .error(placeholderResId)
            .circleCrop()

        if(url?.startsWith("http") == true || url?.startsWith("file:///android_asset") == true){
            Glide.with(view.context).load(url).apply(options).into(view)
        }else{
            Glide.with(view.context).load(placeholderResId).apply(options).into(view)
        }
    }

    override fun loadImageCircle(view: ImageView?, url: String?, borderWidthDp: Float, borderColor: Int) {
        loadImageCircle(view, url, borderWidthDp, borderColor, ImageLoader.placeholderResId)
    }

    override fun loadImageCircle(view: ImageView?, url: String?, borderWidthDp: Float, borderColor: Int, placeholderResId: Int) {
        if (view == null) return
        if(isDestory(view.context)) return

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(placeholderResId)
            .error(placeholderResId)
            .transform(CropCircleWithBorderTransformation(SizeUtils.dp2px(borderWidthDp), borderColor))

        if(url?.startsWith("http") == true || url?.startsWith("file:///android_asset") == true){
            Glide.with(view.context).load(url).apply(options).into(view)
        }else{
            Glide.with(view.context).load(placeholderResId).apply(options).into(view)
        }
    }

    @SuppressLint("ResourceType")
    override fun loadImage(view: ImageView?, url: String?, placeholderResId: Int, isCache: Boolean,radiusdp: Float) {
        if (view == null) return
        if(isDestory(view.context)) return

        val options = RequestOptions()
        if(!isCache){
            options.skipMemoryCache(!isCache)
            options.diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        options.placeholder(placeholderResId)
        options.error(placeholderResId)
        if(radiusdp > 0){
            options.transform(RoundedCornersTransformation(SizeUtils.dp2px(radiusdp), 0))
        }

        if(url?.startsWith("http") == true || url?.startsWith("file:///android_asset") == true){
            Glide.with(view.context).load(url).apply(options)
//              .transition(DrawableTransitionOptions().crossFade()) 如果占位图(placeHolder)比请求加载的url图要大，或者实际加载图是有透明部分未把占位图遮挡，就会看到占位图，占位图被当作加载成功后的图的背景展示
                .transition(DrawableTransitionOptions.with(DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()))
                .into(view)
        }else{
            Glide.with(view.context).load(placeholderResId).apply(options).into(view)
        }
    }

    override fun downloadImage(context: Context?, url: String?, callback: (file: File?) -> Unit) {
        if(context == null || TextUtils.isEmpty(url)){
            callback?.invoke(null)
            return
        }

        Glide.with(context)
            .downloadOnly()
            .load(url)
            .listener(object : RequestListener<File>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    callback?.invoke(null)
                    return false
                }
                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    callback?.invoke(resource)
                    return false
                }
            })
            .preload()
    }

    override fun downloadImageSync(context: Context?, url: String?): File? {
        if(context == null || TextUtils.isEmpty(url)) return null
        return Glide.with(context)
            .downloadOnly()
            .load(url)
            .submit().get()
    }

    override fun getBitmapSync(context: Context?, url: String?, width: Float?, height: Float?): Bitmap? {
        if(context == null || TextUtils.isEmpty(url)) return null
        return Glide.with(context).asBitmap().load(url)
            .run {
                if(width != null && width > 0 && height != null && height > 0){
                    submit(SizeUtils.dp2px(width), SizeUtils.dp2px(height))
                }else{
                    submit()
                }
            }.get()
    }

    override fun getBitmapSync(view: View?, url: String?, width: Float?, height: Float?): Bitmap? {
        if(view == null || TextUtils.isEmpty(url)) return null
        return Glide.with(view).asBitmap().load(url)
            .run {
                if(width != null && width > 0 && height != null && height > 0){
                    submit(SizeUtils.dp2px(width), SizeUtils.dp2px(height))
                }else{
                    submit()
                }
            }.get()
    }

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