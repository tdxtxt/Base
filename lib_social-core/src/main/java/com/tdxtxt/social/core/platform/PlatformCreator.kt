package com.tdxtxt.social.core.platform

import android.content.Context

/**
 * 构建一个平台
 */
interface PlatformCreator {
    fun create(context: Context?, target: Int, params: String? = null): IPlatform?
}