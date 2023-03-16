package com.tdxtxt.base.net.observer

import com.tdxtxt.base.AppConstant
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.net.observer.AbsObserverNetapiResponse

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
abstract class BaseObserverNetapi<T> : AbsObserverNetapiResponse<BaseResponse<T>>() {
    override fun host() = AppConstant.HOST

}