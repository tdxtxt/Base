package com.tdxtxt.social.weibo

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.weibo.utils.WeiboShareHelper

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class WeiboPlatform() : AbsPlatform() {
    private var mShareHelper: WeiboShareHelper? = null
    class Creator constructor() : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return WeiboPlatform()
        }
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        if(mShareHelper == null) mShareHelper = WeiboShareHelper()
        mShareHelper?.share(activity, target, entity, listener, complete)
    }
}