package com.tdxtxt.baselib.callback

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/22
 *     desc   :
 * </pre>
 */
interface Action2<T, R> {
    fun invoke(t: T?, r: R?)
}