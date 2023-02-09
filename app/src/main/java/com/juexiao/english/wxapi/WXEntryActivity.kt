package com.juexiao.english.wxapi

import com.pingerx.socialgo.core.uikit.BaseActionActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : BaseActionActivity(), IWXAPIEventHandler {
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