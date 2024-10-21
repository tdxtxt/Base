package com.tdxtxt.base.net

import com.tdxtxt.base.AppConstant
import com.tdxtxt.logger.LogA
import com.tdxtxt.net.config.DefaultNetProvider
import com.tdxtxt.net.model.AbsResponse

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
class AppNetProvider : DefaultNetProvider() {
    override fun handleError(response: AbsResponse?, errorCode: Int?, errorMsg: String?) {
    }

    override fun printLog(message: String) {
        LogA.i("http::tdx", message)
    }

    override fun host() = AppConstant.HOST
}