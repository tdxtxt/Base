package com.tdxtxt.social.qq

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.qq.utils.QQLoginHelper
import com.tdxtxt.social.qq.utils.QQShareHelper
import com.tencent.tauth.Tencent

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   : https://wiki.connect.qq.com/%e5%88%86%e4%ba%ab%e6%b6%88%e6%81%af%e5%88%b0qq%ef%bc%88%e6%97%a0%e9%9c%80qq%e7%99%bb%e5%bd%95%ef%bc%89
 * </pre>
 */
class QQPlatform constructor(context: Context?, val appId: String?) : AbsPlatform() {
    private val mTencentApi by lazy { Tencent.createInstance(appId, context) }
    private var mLoginHelper: QQLoginHelper? = null
    private var mShareHelper: QQShareHelper? = null
    private var mCompleteCallback: (() -> Unit)? = null
    class Creator constructor(val appId: String?) : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return QQPlatform(context?.applicationContext, appId)
        }
    }

    override fun isInstall(context: Context?): Boolean {
        return mTencentApi.isQQInstalled(context)
    }

    override fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        if(mLoginHelper == null) mLoginHelper = QQLoginHelper(mTencentApi)
        mLoginHelper?.login(activity, listener, complete)
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        if(mShareHelper == null) mShareHelper = QQShareHelper(mTencentApi)
        mShareHelper?.share(activity, target, entity, listener, complete)
    }

    override fun onDestory() {
        mCompleteCallback = null
        mLoginHelper?.onDestory()
        mShareHelper?.onDestory()
        mTencentApi.releaseResource()
    }

}