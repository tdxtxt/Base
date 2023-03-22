package com.tdxtxt.baselib.callback

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/22
 *     desc   :
 * </pre>
 */
interface Action1<T> {
    fun invoke(data: T?)
}