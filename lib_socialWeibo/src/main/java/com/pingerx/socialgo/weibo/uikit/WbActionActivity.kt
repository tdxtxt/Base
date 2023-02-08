package com.pingerx.socialgo.weibo.uikit

import com.pingerx.socialgo.core.uikit.BaseActionActivity
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.share.WbShareCallback

/**
 * @author Pinger
 * @since 2019/1/31 15:15
 */
class WbActionActivity : BaseActionActivity(), WbShareCallback {

    override fun onError(error: UiError?) {
        handleResp("error")
    }

    override fun onCancel() {
        handleResp("cancel")
    }

    override fun onComplete() {
        handleResp("success")
    }
}