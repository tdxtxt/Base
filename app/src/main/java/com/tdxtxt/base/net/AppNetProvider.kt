package com.tdxtxt.base.net

import com.tdxtxt.base.AppConstant
import com.tdxtxt.baselib.tools.LogA
import com.tdxtxt.net.config.DefaultNetProvider

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
class AppNetProvider : DefaultNetProvider() {
    override fun printLog(message: String) {
        LogA.i("http::tdx", message)
    }

    override fun host() = AppConstant.HOST
}