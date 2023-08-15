package com.tdxtxt.social.weibo.utils

import android.app.Activity
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class WeiboShareHelper : IShareAction, Recyclable {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null
    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        mListener = listener
        mComplete = complete
        super.share(activity, target, entity, listener)
    }

    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
    }
    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
    }
    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
    }
    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
    }
    override fun onDestory() {
        mListener = null
        mComplete = null
    }
}