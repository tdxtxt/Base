package com.tdxtxt.net.config

import com.tdxtxt.net.model.AbsResponse
import okhttp3.OkHttpClient

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络抽象配置操作
 * </pre>
 */
interface NetProvider {
    fun host(): String
    fun handleError(response: AbsResponse?, errorCode: Int?, errorMsg: String?)
    fun throwable2Message(e: Throwable?): String
    fun throwable2Code(e: Throwable?): Int
    fun throwable2Response(e: Throwable?): AbsResponse?
    fun createOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient
    fun configHttpClient(builder: OkHttpClient.Builder)
    fun getRequestHandler(): RequestHandler?
    fun printLog(message: String)
}