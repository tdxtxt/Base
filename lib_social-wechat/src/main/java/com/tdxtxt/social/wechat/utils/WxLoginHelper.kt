package com.tdxtxt.social.wechat.utils

import android.content.Context
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI

/**
 * 微信登录助手
 */
class WxLoginHelper(context: Context?, private val wxapi: IWXAPI?): Recyclable {
    private var mListener: OnLoginListener? = null
    /**
     * 开始登录
     */
    fun login(listener: OnLoginListener?) {
        this.mListener = listener
        // 发起请求，wxEntry将会获得code，接着获取access_token
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "carjob_wx_login"
        wxapi?.sendReq(req)
    }

    fun getListener() = mListener

    override fun onDestory() {
        mListener = null
    }

}
