package com.tdxtxt.social.dingtalk.activity

import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler
import com.android.dingtalk.share.ddsharemodule.message.BaseReq
import com.android.dingtalk.share.ddsharemodule.message.BaseResp
import com.tdxtxt.social.core.activity.BaseActionActivity

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/9/9
 *     desc   :
 * </pre>
 */
open class DingtalkActionActivity : BaseActionActivity(), IDDAPIEventHandler {
    override fun onReq(req: BaseReq?) {
        handleReq(req)
    }

    override fun onResp(resp: BaseResp?) {
        handleResp(resp)
    }
}