package com.tdxtxt.net.observer

import com.tdxtxt.net.NetMgr
import io.reactivex.observers.DisposableObserver

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   :
 * </pre>
 */
abstract class AbsObserverNetapi2 <T> : DisposableObserver<T>() {
    abstract fun host(): String

    abstract fun handleResp(response: T)

    abstract fun onFailure(errorCode: Int?, errorMsg: String?, e: Throwable?)

    override fun onNext(response: T) {
        handleResp(response)

        onComplete()
    }

    override fun onError(e: Throwable) {
        val provider = getProvider()
        val message = provider.throwable2Message(e)
        val code = provider.throwable2Code(e)
        provider.handleError(null, code, message)
        onFailure(code, message, e)
        onComplete()
    }

    override fun onComplete() {
    }

    fun getProvider() = NetMgr.getProvider(host())
}