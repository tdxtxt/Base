package com.tdxtxt.social.core.lisenter

import com.tdxtxt.social.core.bean.AuthInfo

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
abstract class OnLoginListener {
    abstract fun onSuccess(authCode: AuthInfo)
    abstract fun onCancel()
    abstract fun onFailure(msg: String?)

    open fun onStart(){}
    open fun printLog(log: String?){}
}