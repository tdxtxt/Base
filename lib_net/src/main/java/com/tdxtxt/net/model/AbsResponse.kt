package com.tdxtxt.net.model

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络请求返回对象抽象
 * </pre>
 */
interface AbsResponse {
    //状态成功
    fun isSuccess(): Boolean
    //后台返回信息
    fun getMessage(): String?
    //code
    fun getCode(): Int?
    //数据信息
    fun getMeta(): Any?
}