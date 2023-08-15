package com.tdxtxt.social.core.lisenter

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
abstract class OnPayListener {
    abstract fun onSuccess()
    abstract fun onCancel()
    abstract fun onFailure(msg: String?)

    open fun onStart(){}
    open fun printLog(log: String?){}
}