package com.tdxtxt.base.net

import com.tdxtxt.base.AppConstant
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.net.NetMgr
import io.reactivex.Observable

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
object AppRepository {
    private fun getService() = NetMgr.getService(AppConstant.HOST, InterfaceApi::class.java)

    fun queryArticleList(pageNum: Int): Observable<BaseResponse<Any>> {
        return getService().queryArticleList(pageNum)
    }
}