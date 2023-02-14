package com.tdxtxt.net.config

import okhttp3.Interceptor
import okhttp3.Response

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络请求拦截器
 * </pre>
 */
class NetInterceptor constructor(private val handler: RequestHandler?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        handler?.apply {
            request = onBeforeRequest(request, chain)
        }
        var response = chain.proceed(request)
        handler?.apply {
            response = onAfterRequest(response, chain)
        }
        return response
    }
}