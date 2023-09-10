package com.tdxtxt.social.dingtalk

import android.app.Activity
import android.content.Context
import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler
import com.android.dingtalk.share.ddsharemodule.ShareConstant
import com.android.dingtalk.share.ddsharemodule.message.BaseResp
import com.android.dingtalk.share.ddsharemodule.message.SendAuth
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.dingtalk.utils.DingtalkShareHelper

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   : https://open.dingtalk.com/document/mobile-app-guide/android-sharing-sdk-access-process
 * </pre>
 */
class DingtalkPlatform(val context: Context?, val appId: String?) : AbsPlatform() {
    private val mDingtalkApi by lazy { DDShareApiFactory.createDDShareApi(context, appId, false) }
    private var mShareHelper: DingtalkShareHelper? = null
    private var mCompleteCallback: (() -> Unit)? = null
    class Creator constructor(val appId: String?) : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return DingtalkPlatform(context, appId)
        }
    }

    override fun isInstall(context: Context?): Boolean {
        return mDingtalkApi.isDDAppInstalled
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if(mShareHelper == null) mShareHelper = DingtalkShareHelper(mDingtalkApi)
        mShareHelper?.share(activity, target, entity, listener, complete)
    }

    override fun handleIntent(activity: Activity) {
        super.handleIntent(activity)
        if (activity is IDDAPIEventHandler) {
            mDingtalkApi.handleIntent(activity.intent, activity)
        }
    }

    override fun onResponse(resp: Any?) {
        if(resp !is BaseResp) return
        val errCode: Int = resp.mErrCode
        val errMsg: String = resp.mErrStr

        if(resp.type == ShareConstant.COMMAND_SENDAUTH_V2 && (resp is SendAuth.Resp)){ //登录
            when(errCode){
                BaseResp.ErrCode.ERR_OK -> { //授权成功，授权码为

                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> { //授权取消

                }
                else -> { //授权异常

                }
            }
        }else{ //分享
            val listener = mShareHelper?.getListener()
            listener?.printLog("$errMsg:$errCode")
            when(errCode){
                BaseResp.ErrCode.ERR_OK -> { //分享成功
                    listener?.onSuccess()
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> { //分享取消
                    listener?.onCancel()
                }
                else -> { //分享失败
                    listener?.onFailure("分享失败")
                }
            }
        }

        mCompleteCallback?.invoke()
    }

    override fun onDestory() {
        super.onDestory()
        mShareHelper?.onDestory()
        mCompleteCallback = null
    }
}