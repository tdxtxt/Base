package com.tdxtxt.net.observer

import com.tdxtxt.net.model.AbsResponse
import com.tdxtxt.net.NetMgr
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

    abstract fun onFailure(errorCode: Int?, errorMsg: String?, errorData: Any?)

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
        provider.handleError(data, code, message)
        onFailure(code, message, data)
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