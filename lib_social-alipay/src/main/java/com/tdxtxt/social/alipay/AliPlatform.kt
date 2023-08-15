package com.tdxtxt.social.alipay

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.alipay.utils.AliLoginHelper
import com.tdxtxt.social.alipay.utils.AliPayHelper
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class AliPlatform constructor(context: Context?): AbsPlatform() {
    private var mLoginHelper: AliLoginHelper? = null
    private var mPayHelper: AliPayHelper? = null
    private var mCompleteCallback: (() -> Unit)? = null

    class Creator constructor() : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return AliPlatform(context?.applicationContext)
        }
    }

    override fun doPay(context: Context?, params: String?, listener: OnPayListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if(mPayHelper == null) mPayHelper = AliPayHelper()
        mPayHelper?.doPay(context, params, listener, complete)
    }

    override fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if(mLoginHelper == null) mLoginHelper = AliLoginHelper()
        mLoginHelper?.login(activity, null, listener, complete)
    }

    override fun onDestory() {
        mLoginHelper?.onDestory()
        mPayHelper?.onDestory()
    }

}