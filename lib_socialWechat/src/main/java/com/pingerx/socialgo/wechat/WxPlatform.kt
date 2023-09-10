package com.pingerx.socialgo.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.SocialGoConfig
import com.pingerx.socialgo.core.exception.SocialError
import com.pingerx.socialgo.core.listener.OnLoginListener
import com.pingerx.socialgo.core.listener.OnPayListener
import com.pingerx.socialgo.core.listener.OnShareListener
import com.pingerx.socialgo.core.model.LoginResult
import com.pingerx.socialgo.core.model.ShareEntity
import com.pingerx.socialgo.core.platform.AbsPlatform
import com.pingerx.socialgo.core.platform.IPlatform
import com.pingerx.socialgo.core.platform.PlatformCreator
import com.pingerx.socialgo.core.platform.Target
import com.pingerx.socialgo.core.utils.SocialGoUtils
import com.pingerx.socialgo.core.utils.SocialLogUtils
import com.pingerx.socialgo.wechat.uikit.WxActionActivity
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONException
import org.json.JSONObject

/**
 * 微信平台
 * [分享与收藏文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317340&token=&lang=zh_CN)
 * [微信登录文档](https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN)
 *
 * 缩略图不超过 32kb
 * 源文件不超过 10M
 */
class WxPlatform constructor(context: Context, appId: String?, private val wxSecret: String?, appName: String?) : AbsPlatform(appId, appName) {

    private var mLoginHelper: WxLoginHelper? = null
    private var mShareHelper: WxShareHelper? = null
    private var mPayListener: OnPayListener? = null
    private var mWxApi: IWXAPI = WXAPIFactory.createWXAPI(context, appId, false)

    class Creator : PlatformCreator {
        override fun create(context: Context, target: Int, targetAction: Int, params: String?): IPlatform {
            val config = SocialGo.getConfig()
            if (SocialGoUtils.isAnyEmpty(config?.getWxAppId(), config?.getWxSecretKey())) {
                throw IllegalArgumentException(SocialError.MSG_WX_ID_NULL)
            }
            return WxPlatform(context, config?.getWxAppId(), config?.getWxSecretKey(), config?.getAppName()).apply {
                setTarget(target)
                setTargetAction(targetAction)
                setParmas(params)
            }
        }
    }

    init {
        mWxApi.registerApp(appId)
    }

    override fun checkPlatformConfig(): Boolean {
        return super.checkPlatformConfig() && !TextUtils.isEmpty(wxSecret)
    }

    override fun isInstall(context: Context): Boolean {
        return mWxApi.isWXAppInstalled
    }

    override fun openMiniProgram(context: Context, miniProgramId: String, path: String, isDebug: Boolean) {
        val req = WXLaunchMiniProgram.Req()
        req.apply {
            this.userName = miniProgramId
            this.path = path
            this.miniprogramType = if(isDebug) WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW else WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        }
        mWxApi.sendReq(req)
    }

    override fun recycle() {
        mWxApi.detach()
    }

    override fun handleIntent(activity: Activity) {
        if (activity is IWXAPIEventHandler) {
            mWxApi.handleIntent(activity.intent, activity as IWXAPIEventHandler)
        }
    }

    override fun getActionClazz(): Class<*> {
        return WxActionActivity::class.java
    }

    override fun onResponse(resp: Any?) {
        if (resp !is BaseResp) {
            return
        }
        when(resp){
            is SendAuth.Resp ->{
                Log.i("tdxtxt:WxPlatform","微信回调结果:errCode=${resp.errCode}; errStr=${resp.errStr}; authResult=${resp.authResult}; code=${resp.code};")
            }
            else -> {
                Log.i("tdxtxt:WxPlatform","微信回调结果:errCode=${resp.errCode}; errStr=${resp.errStr};")
            }
        }

        when {
            resp.type == ConstantsAPI.COMMAND_SENDAUTH -> {
                // 登录
                val listener = mLoginHelper?.getLoginListener()
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> {
                        // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                        val authResp = resp as SendAuth.Resp
                        // 这个code如果需要使用微信充值功能的话，服务端需要使用
                        // 这里为了安全暂时不提供出去
                        val authCode = authResp.code
                        if (SocialGo.getConfig()?.isOnlyAuthCode() == true) {
                            listener?.getFunction()?.onLoginSuccess?.invoke(LoginResult(Target.LOGIN_WX, authCode))
                        } else {
                            mLoginHelper?.getAccessTokenByCode(authCode)
                        }
                    }
                    BaseResp.ErrCode.ERR_USER_CANCEL ->
                        // 用户取消
                        listener?.getFunction()?.onCancel?.invoke()
                    BaseResp.ErrCode.ERR_AUTH_DENIED ->
                        // 用户拒绝授权
                        listener?.getFunction()?.onCancel?.invoke()
                }
            }
            resp.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK ->            // 分享成功
                        mShareHelper?.onSuccess()
                    BaseResp.ErrCode.ERR_USER_CANCEL ->   // 分享取消
                        mShareHelper?.onCancel()
                    BaseResp.ErrCode.ERR_SENT_FAILED ->   // 分享失败
                        mShareHelper?.onFailure(SocialError(SocialError.CODE_SDK_ERROR, "分享失败"))
                    BaseResp.ErrCode.ERR_AUTH_DENIED ->   // 分享被拒绝
                        mShareHelper?.onFailure(SocialError(SocialError.CODE_SDK_ERROR, "分享被拒绝"))
                }
            }
            resp.type == ConstantsAPI.COMMAND_PAY_BY_WX -> onPayResp(resp.errCode)
        }
    }

    override fun onReq(activity: Activity, req: Any?) {
        if (req !is BaseReq) {
            return
        }
        //把消息广播出去
        if (req.type == ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX && req is ShowMessageFromWX.Req) {
            val mediaMsg = req.message
            val extInfo = mediaMsg.messageExt

            SocialLogUtils.e(extInfo)

            val intent = Intent(SocialGo.getConfig()?.getStartAction())
            intent.putExtra("extra", extInfo)
            activity.sendBroadcast(intent)
        }
    }

    override fun login(activity: Activity, listener: OnLoginListener) {
        if (mLoginHelper == null) {
            mLoginHelper = WxLoginHelper(activity, mWxApi, appId)
        }
        mLoginHelper!!.login(wxSecret, listener)
    }

    override fun share(activity: Activity, target: Int, entity: ShareEntity, listener: OnShareListener) {
        if (mShareHelper == null) {
            mShareHelper = WxShareHelper(mWxApi)
        }
        mShareHelper!!.share(activity, target, entity, listener)
    }


    override fun doPay(context: Context, params: String, listener: OnPayListener) {
        // 判断微信当前版本是否支持支付
        if (!mWxApi.isWXAppInstalled || mWxApi.wxAppSupportAPI < Build.PAY_SUPPORTED_SDK_INT) {
            listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_VERSION_LOW))
            return
        }
        mPayListener = listener
        val json: JSONObject
        try {
            json = JSONObject(params)
        } catch (e: JSONException) {
            e.printStackTrace()
            listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_PAY_PARAM_ERROR))
            return
        }
        if (TextUtils.isEmpty(json.optString("appid")) || TextUtils.isEmpty(json.optString("partnerid"))
                || TextUtils.isEmpty(json.optString("prepayid")) || TextUtils.isEmpty(json.optString("package")) ||
                TextUtils.isEmpty(json.optString("noncestr")) || TextUtils.isEmpty(json.optString("timestamp")) ||
                TextUtils.isEmpty(json.optString("sign"))) {
            listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_PAY_PARAM_ERROR))
            return
        }

        val req = PayReq()
        // https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2
        req.appId = json.optString("appid") //应用ID
        req.partnerId = json.optString("partnerid") //商户号
        req.prepayId = json.optString("prepayid") //预支付交易会话ID
        req.packageValue = json.optString("package") //扩展字段 固定写死 Sign=WXPay
        req.nonceStr = json.optString("noncestr") //随机字符串
        req.timeStamp = json.optString("timestamp") //时间戳
        req.sign = json.optString("sign") //签名
        val check = req.checkArgs()
        val sendReq = mWxApi.sendReq(req)
        Log.i("tdxtxt:WxPlatform", "微信支付请求：checkArgs=$check;sendReq=$sendReq")
        listener.getFunction().printLog?.invoke("WxPlatform::微信支付请求：checkArgs=$check;sendReq=$sendReq")
    }

    /**
     * 支付回调响应
     */
    private fun onPayResp(code: Int) {
        when (code) {
            BaseResp.ErrCode.ERR_OK -> //成功
                mPayListener?.getFunction()?.onSuccess?.invoke()
            BaseResp.ErrCode.ERR_USER_CANCEL -> //取消
                mPayListener?.getFunction()?.onCancel?.invoke()
            BaseResp.ErrCode.ERR_COMM -> //公共错误
                mPayListener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
            BaseResp.ErrCode.ERR_BAN -> //支付被禁止
                mPayListener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
            BaseResp.ErrCode.ERR_AUTH_DENIED -> //权限验证失败
                mPayListener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
            BaseResp.ErrCode.ERR_SENT_FAILED -> //请求支付失败
                mPayListener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
            BaseResp.ErrCode.ERR_UNSUPPORT -> //不支持此操作
                mPayListener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
        }
        mPayListener = null
    }
}
