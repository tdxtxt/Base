package com.tdxtxt.base.net.observer

import com.tdxtxt.base.AppConstant
import com.tdxtxt.net.observer.AbsObserverNetapi

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
abstract class BaseObserverNetapi2<T> : AbsObserverNetapi<T>() {
    override fun host() = AppConstant.HOST

}