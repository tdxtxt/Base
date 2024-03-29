package com.tdxtxt.baselib.image

import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.image.glide.GlideImageLoader

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/22
 *     desc   :
 * </pre>
 */
object ImageLoader : ILoader by GlideImageLoader{
    var placeholderResId: Int = R.drawable.baselib_image_placeholder
}