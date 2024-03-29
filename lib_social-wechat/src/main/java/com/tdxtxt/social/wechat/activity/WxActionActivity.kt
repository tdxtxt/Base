package com.tdxtxt.social.wechat.activity

import com.tdxtxt.social.core.activity.BaseActionActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

/**
 * @author Pinger
 * @since 2019/1/31 14:54
 */
open class WxActionActivity : BaseActionActivity(), IWXAPIEventHandler {

    override fun onResp(resp: BaseResp) {
        handleResp(resp)
    }

    /**
     * 从微信启动App
     * @param req
     */
    override fun onReq(req: BaseReq?) {
        handleReq(req)
    }
}