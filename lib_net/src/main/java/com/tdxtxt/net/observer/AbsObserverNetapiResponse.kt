package com.tdxtxt.net.observer

import com.tdxtxt.net.NetMgr
import com.tdxtxt.net.model.AbsResponse
import io.reactivex.observers.DisposableObserver

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   :
 * </pre>
 */
abstract class AbsObserverNetapiResponse <R : AbsResponse> : DisposableObserver<R>() {
    abstract fun host(): String

    abstract fun onSuccess(response: R)

    /**
     * errorBody:如果接口返回非200状态码，此时为响应报文中的ErrorBody数据内容，如果返回200状态码，此时为AbsResponse.getMeta()对象
     */
    abstract fun onFailure(errorCode: Int?, errorMsg: String?, errorBody: Any?)

    /**
     * @return true 使用NetProvider.handleError方法处理错误; false 不使用NetProvider.handleError方法处理错误
     */
    open fun useProviderHandleError(errorCode: Int?, errorMsg: String?, errorBody: Any?): Boolean {
        return true
    }

    override fun onNext(response: R) {
        if(filter(response)){
            onSuccess(response)
        }else{
            onFailure(response.getCode(), response.getMessage(), response.getMeta())
        }

        onComplete()
    }

    override fun onError(e: Throwable) {
        val provider = getProvider()
        val message = provider.throwable2Message(e)
        val code = provider.throwable2Code(e)
        val data = provider.throwable2Response(e)
        val errorBody = provider.throwable2ErrorBody(e)
        if(useProviderHandleError(code, message, errorBody)) provider.handleError(data, code, message)
        onFailure(code, message, errorBody)
        onComplete()
    }

    private fun getProvider() = NetMgr.getProvider(host())

    override fun onComplete() {

    }

    fun filter(response: R?): Boolean{
        if(response == null) return false

        if(response.isSuccess()){
            return true
        }

        getProvider().handleError(response, response.getCode(), response.getMessage())
        return false
    }
}