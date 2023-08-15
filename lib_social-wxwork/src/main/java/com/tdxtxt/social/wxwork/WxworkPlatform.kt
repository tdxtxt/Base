package com.tdxtxt.social.wxwork

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.wxwork.utils.WxworkLoginHelper
import com.tdxtxt.social.wxwork.utils.WxworkShareHelper
import com.tencent.wework.api.WWAPIFactory

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   : https://developer.work.weixin.qq.com/document/path/91196
 * </pre>
 */
class WxworkPlatform(context: Context?, val schema: String?) : AbsPlatform() {
    private val mWxworkApi by lazy { WWAPIFactory.createWWAPI(context)?.apply { registerApp(schema) }}
    private var mShareHelper: WxworkShareHelper? = null
    private var mLoginHelper: WxworkLoginHelper? = null
    class Creator constructor(val schema: String?) : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return WxworkPlatform(context?.applicationContext, schema)
        }
    }

    override fun isInstall(context: Context?): Boolean {
        return mWxworkApi?.isWWAppInstalled?: false
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        if(mShareHelper == null) mShareHelper = WxworkShareHelper(mWxworkApi)
        mShareHelper?.share(activity, target, entity, listener, complete)
    }

    override fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        if(mLoginHelper == null) mLoginHelper = WxworkLoginHelper()
    }

    override fun onDestory() {
        mLoginHelper?.onDestory()
        mShareHelper?.onDestory()
        mWxworkApi?.detach()
    }
}