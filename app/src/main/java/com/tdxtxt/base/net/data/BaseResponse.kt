package com.tdxtxt.base.net.data

import com.tdxtxt.net.model.AbsResponse

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
data class BaseResponse <T> constructor(val msg: String?, val code: Int, val data: T?) : AbsResponse<T> {

    override fun isSuccess() = code == 0

    override fun getMessage() = msg

    override fun getCode() = code
}