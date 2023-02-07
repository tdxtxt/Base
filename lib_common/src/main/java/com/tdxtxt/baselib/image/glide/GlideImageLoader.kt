package com.baselib.helper.image.glide

import android.annotation.SuppressLint
import android.widget.ImageView
import com.baselib.helper.image.ILoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifOptions
import com.tdxtxt.baselib.R
import java.io.File

/**
 * 创建时间： 2020/5/25
 * 编码： tangdex
 * 功能描述:
 */
object GlideImageLoader: ILoader {

  override fun init(){

  }

    override fun toggleGif(view: ImageView?, resId: Int, resume: Boolean) {
        if(view == null) return
        if (view.drawable is GifDrawable) {
            val drawable = view.drawable as GifDrawable
            if(resume){
                if(!drawable.isRunning) drawable.start()
            }else{
                if (drawable.isRunning) drawable.stop()
            }
        }else{
            Glide.with(view.context).load(resId).set(GifOptions.DECODE_FORMAT, DecodeFormat.DEFAULT).into(view)

            if (view.drawable is GifDrawable) {
                val drawable = view.drawable as GifDrawable
                if(resume){
                    if(!drawable.isRunning) drawable.start()
                }else{
                    if (drawable.isRunning) drawable.stop()
                }
            }
        }
    }

    override fun loadImage(view: ImageView?, resId: Int){
      if(view == null) return
//    view?.setImageResource(resId)
        Glide.with(view.context).load(resId).into(view)
  }

  override fun loadImage(view: ImageView?, url: String) {
      loadImage(view, url, R.drawable.baselib_image_placeholder, true)
  }

  override fun loadImage(view: ImageView?, url: String, placeholderResId: Int) {
    loadImage(view, url, placeholderResId, true)
  }

  @SuppressLint("ResourceType")
  override fun loadImage(view: ImageView?, url: String,  placeholderResId: Int, isCache: Boolean) {
      if(view == null) return
      val requests = Glide.with(view.context)
      val request = requests.load(url)
      request.transition(DrawableTransitionOptions().crossFade())
      when{
          placeholderResId > 0 -> {
              request.placeholder(placeholderResId)
              request.error(placeholderResId)
          }
          !isCache ->{
              // 跳过内存缓存
              request.skipMemoryCache(true)
              // 跳过磁盘缓存
              request.diskCacheStrategy(DiskCacheStrategy.NONE)
          }
      }
      request.into(view)
  }

  override fun saveImage(
      url: String,
      destFile: File,
      callback: (isSuccess: Boolean, msg: String) -> Unit) {
     }


  override fun clearMemoryCache() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun clearDiskCache() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}