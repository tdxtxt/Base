package com.tdxtxt.social.qq.utils

import android.app.Activity
import com.tdxtxt.social.core.bean.AuthInfo
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class QQLoginHelper (val tencentApi: Tencent) : Recyclable, IUiListener {
    private var mListener: OnLoginListener? = null
    private var mComplete: (() -> Unit)? = null
    fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        this.mListener = listener
        this.mComplete = complete
        tencentApi.login(activity, "all", this)
    }

    override fun onDestory() {
        mComplete = null
        mListener = null
    }

    override fun onComplete(result: Any?) {
        if(result is JSONObject){
            val authInfo = AuthInfo()
            mListener?.onSuccess(authInfo)
        }else{
            mListener?.onFailure("返回数据错误")
        }
        mComplete?.invoke()
    }

    override fun onError(error: UiError?) {
        mListener?.onFailure(error?.errorMessage)
        mComplete?.invoke()
    }

    override fun onCancel() {
        mListener?.onCancel()
        mComplete?.invoke()
    }

    override fun onWarning(p0: Int) {

    }
}