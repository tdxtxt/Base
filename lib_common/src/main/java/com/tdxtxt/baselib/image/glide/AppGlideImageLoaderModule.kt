package com.tdxtxt.baselib.image.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 *  ime   : 2018/6/28
 *  desc   : Glide官方推荐的方式:
 *              生成 https://bumptech.github.io/glide/doc/generatedapi.html
 *              参考 https://juejin.im/post/5a3295356fb9a0451464128c
 *  使用该方式，才能使用官方推荐的方式 {@link GlideApp#with(View)}；否则Glide写法不一样 {@link Glide#with(View)}
 */
@GlideModule
class AppGlideImageLoaderModule : AppGlideModule() {
  //全局配置glide选项
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    //全局设置图片格式为RGB_565
//    builder.setDiskCache{ FileHelper.getImageDir(context)}
    builder.setLogLevel(Log.VERBOSE)
           .setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    super.applyOptions(context, builder)
  }
  //注册自定义组件
  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    val builder = OkHttpClient.Builder()
//    builder.addInterceptor(ProgressInterceptor())
    val okHttpClient = builder.build()
    // 把URLConnection替换为OkHttp
    registry.replace(GlideUrl::class.java, InputStream::class.java,OkHttpUrlLoader.Factory(okHttpClient))
  }
}