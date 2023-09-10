package com.tdxtxt.social.wechat

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.core.bean.AuthInfo
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.AbsPlatform
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.wechat.activity.WxActionActivity
import com.tdxtxt.social.wechat.utils.WxLoginHelper
import com.tdxtxt.social.wechat.utils.WxPayHelper
import com.tdxtxt.social.wechat.utils.WxShareHelper
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   : https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
 * </pre>
 */
class WxPlatform constructor(context: Context?, appId: String?, wxSecret: String?): AbsPlatform() {
    private val mWxApi by lazy { WXAPIFactory.createWXAPI(context, appId, false).apply { registerApp(appId) } }
    private var mLoginHelper: WxLoginHelper? = null
    private var mShareHelper: WxShareHelper? = null
    private var mPayHelper: WxPayHelper? = null
    private var mCompleteCallback: (() -> Unit)? = null

    class Creator constructor(val appId: String?, val wxSecret: String?) : PlatformCreator {
        override fun create(context: Context?, target: Int, params: String?): IPlatform? {
            return WxPlatform(context?.applicationContext, appId, wxSecret)
        }
    }

    override fun getActionClazz(): Class<*> {
        return WxActionActivity::class.java
    }

    override fun isInstall(context: Context?): Boolean {
        return mWxApi.isWXAppInstalled
    }

    override fun openMiniProgram(context: Context?, miniProgramId: String?, path: String?, isDebug: Boolean, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        val req = WXLaunchMiniProgram.Req()
        req.apply {
            this.userName = miniProgramId
            this.path = path
            this.miniprogramType = if(isDebug) WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW else WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        }
        mWxApi.sendReq(req)
    }

    override fun handleIntent(activity: Activity) {
        super.handleIntent(activity)
        if (activity is IWXAPIEventHandler) {
            mWxApi.handleIntent(activity.intent, activity)
        }
    }

    override fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if (mLoginHelper == null) mLoginHelper = WxLoginHelper(activity, mWxApi)
        mLoginHelper?.login(listener)
    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if(mShareHelper == null) mShareHelper = WxShareHelper(mWxApi)
        mShareHelper?.share(activity, target, entity, listener, complete)
    }

    override fun doPay(context: Context?, params: String?, listener: OnPayListener?, complete: (() -> Unit)?) {
        this.mCompleteCallback = complete
        if(mPayHelper == null) mPayHelper = WxPayHelper(mWxApi)
        mPayHelper?.doPay(params, listener, complete)
    }

    override fun onResponse(resp: Any?) {
        super.onResponse(resp)
        if(resp !is BaseResp) return
        val logMsg = if(resp is SendAuth.Resp){
            "微信回调结果:errCode=${resp.errCode}; errStr=${resp.errStr}; authResult=${resp.authResult}; code=${resp.code}"
        }else{
            "微信回调结果:errCode=${resp.errCode}; errStr=${resp.errStr};"
        }
        when {
            resp.type == ConstantsAPI.COMMAND_SENDAUTH -> {
                // 登录
                val listener = mLoginHelper?.getListener()
                listener?.printLog(logMsg)
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> {
                        // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                        val authResp = resp as SendAuth.Resp
                        // 这个code如果需要使用微信充值功能的话，服务端需要使用
                        // 这里为了安全暂时不提供出去
                        val authInfo = AuthInfo()
                        authInfo.wechatAuthCode = authResp.code
                        listener?.onSuccess(authInfo)
                    }
                    BaseResp.ErrCode.ERR_USER_CANCEL ->
                        // 用户取消
                        listener?.onCancel()
                    BaseResp.ErrCode.ERR_AUTH_DENIED ->
                        // 用户拒绝授权
                        listener?.onCancel()
                }
            }
            resp.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
                // 分享
                val listener = mShareHelper?.getListener()
                listener?.printLog(logMsg)
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK ->            // 分享成功
                        listener?.onSuccess()
                    BaseResp.ErrCode.ERR_USER_CANCEL ->   // 分享取消
                        listener?.onCancel()
                    BaseResp.ErrCode.ERR_SENT_FAILED ->   // 分享失败
                        listener?.onFailure("分享失败")
                    BaseResp.ErrCode.ERR_AUTH_DENIED ->   // 分享被拒绝
                        listener?.onFailure("分享被拒绝")
                }
            }
            resp.type == ConstantsAPI.COMMAND_PAY_BY_WX -> {
                //支付
                val listener = mPayHelper?.getListener()
                listener?.printLog(logMsg)
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> //成功
                        listener?.onSuccess()
                    BaseResp.ErrCode.ERR_USER_CANCEL -> //取消
                        listener?.onCancel()
                    BaseResp.ErrCode.ERR_COMM -> //公共错误
                        listener?.onFailure("公共错误")
                    BaseResp.ErrCode.ERR_BAN -> //支付被禁止
                        listener?.onFailure("支付被禁止")
                    BaseResp.ErrCode.ERR_AUTH_DENIED -> //权限验证失败
                        listener?.onFailure("权限验证失败")
                    BaseResp.ErrCode.ERR_SENT_FAILED -> //请求支付失败
                        listener?.onFailure("请求支付失败")
                    BaseResp.ErrCode.ERR_UNSUPPORT -> //不支持此操作
                        listener?.onFailure("不支持此操作")
                }
            }
        }

        mCompleteCallback?.invoke()
    }

    override fun onDestory() {
        super.onDestory()
        mShareHelper?.onDestory()
        mPayHelper?.onDestory()
        mLoginHelper?.onDestory()
        mCompleteCallback = null
        mWxApi.detach()
    }

}