package com.tdxtxt.social.android

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.android.utils.AndroidShareHelper
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
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
class AndroidPlatform() : AbsPlatform() {
    private var mShareHelper: AndroidShareHelper? = null
    class Creator constructor() : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return AndroidPlatform()
        }
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        if(mShareHelper == null) mShareHelper = AndroidShareHelper()
        mShareHelper?.share(activity, target, entity, listener, complete)
    }

    override fun onDestory() {
        super.onDestory()
        mShareHelper?.onDestory()
    }
}