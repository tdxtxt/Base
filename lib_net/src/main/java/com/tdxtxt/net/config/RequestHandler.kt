package com.tdxtxt.net.config

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络请求抽象
 * </pre>
 */
interface RequestHandler {
    //请求前操作
    fun onBeforeRequest(request: Request, chain: Interceptor.Chain): Request

    //请求后操作
    fun onAfterRequest(response: Response, chain: Interceptor.Chain): Response
}