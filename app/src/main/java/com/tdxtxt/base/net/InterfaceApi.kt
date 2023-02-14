package com.tdxtxt.base.net

import com.tdxtxt.base.net.data.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
interface InterfaceApi {
    @GET("/article/list/{pageNum}/json")
    fun queryArticleList(@Path("pageNum") pageNum: Int): Observable<BaseResponse<Any>>
}