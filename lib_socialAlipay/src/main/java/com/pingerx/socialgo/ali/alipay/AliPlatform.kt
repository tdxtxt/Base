package com.pingerx.socialgo.ali.alipay

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.alipay.sdk.app.AuthTask
import com.alipay.sdk.app.PayTask
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.exception.SocialError
import com.pingerx.socialgo.core.listener.OnLoginListener
import com.pingerx.socialgo.core.listener.OnPayListener
import com.pingerx.socialgo.core.platform.AbsPlatform
import com.pingerx.socialgo.core.platform.IPlatform
import com.pingerx.socialgo.core.platform.PlatformCreator
import com.pingerx.socialgo.core.platform.Target
import com.pingerx.socialgo.ali.uikit.AliActionActivity
import com.pingerx.socialgo.core.model.LoginResult
import com.pingerx.socialgo.qq.model.AliAccessToken
import com.pingerx.socialgo.qq.model.AliUser

/**
 * 支付宝支付平台
 */
class AliPlatform(appId: String?, appName: String?) : AbsPlatform(appId, appName) {

    class Creator : PlatformCreator {
        override fun create(context: Context, target: Int, targetAction: Int, params: String?): IPlatform {
            return AliPlatform("", SocialGo.getConfig()?.getAppName()).apply {
                setTarget(target)
                setTargetAction(targetAction)
                setParmas(params)
            }
        }
    }

    override fun getActionClazz(): Class<*> {
        return AliActionActivity::class.java
    }

    override fun isInstall(context: Context): Boolean {
//        val uri = Uri.parse("alipays://platformapi/startApp")
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        val componentName = intent.resolveActivity(context.packageManager)
//        return componentName != null

        return true
    }

    override fun openMiniProgram(context: Context, miniProgramId: String, path: String, isDebug: Boolean) {
        Log.i("tdxtxt:AliPlatform","支付宝打开小程序:暂未实现")
    }


    /**
     * https://opendocs.alipay.com/open/204/105296
     */
    override fun doPay(context: Context, params: String, listener: OnPayListener) {
        Log.i("tdxtxt:AliPlatform","支付宝支付开始参数:$params")
        listener.getFunction().printLog?.invoke("AliPlatform::支付宝支付开始参数:$params")
        Thread{
            try{
                Log.i("tdxtxt:AliPlatform","支付宝支付开始-子线程(${Thread.currentThread().name});参数：$params")
                listener.getFunction().printLog?.invoke("AliPlatform::支付宝支付开始-子线程(${Thread.currentThread().name});参数：$params")
                val payTask = PayTask(context as Activity)
                Log.i("tdxtxt:AliPlatform", "支付宝版本:${payTask.version}")
                listener.getFunction().printLog?.invoke("AliPlatform::支付宝版本:${payTask.version}")
                val payResult = payTask.payV2(params, true)
                Log.i("tdxtxt:AliPlatform","支付宝支付结果:$payResult")
                listener.getFunction().printLog?.invoke("AliPlatform::支付宝支付结果:$payResult")
                SocialGo.getHandler().post {
                    Log.i("tdxtxt:AliPlatform","切换主线程处理支付结果")
                    listener.getFunction().printLog?.invoke("AliPlatform::切换主线程处理支付结果")
                    if (payResult != null) {
                        val resultStatus = payResult["resultStatus"]
                        when {
                            TextUtils.equals(resultStatus, "9000") -> //支付成功
                                listener.getFunction().onSuccess?.invoke()
                            TextUtils.equals(resultStatus, "8000") -> //支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                listener.getFunction().onDealing?.invoke()
                            TextUtils.equals(resultStatus, "6001") -> //支付取消
                                listener.getFunction().onCancel?.invoke()
                            TextUtils.equals(resultStatus, "6002") -> //网络连接出错
                                listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_REQUEST_ERROR))
                            TextUtils.equals(resultStatus, "4000") -> //支付错误
                                listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_PAY_ERROR))
                            TextUtils.equals(resultStatus, "7001") -> //待支付订单存在
                                listener.getFunction().onFailure?.invoke(SocialError(-1, "已有支付界面，请前先前往支付宝应用取消存在支付界面，再返回支付"))
                            TextUtils.equals(resultStatus, "5000") -> //待支付订单存在
                                listener.getFunction().onFailure?.invoke(SocialError(-1, "已有支付界面，请前先前往支付宝应用取消存在支付界面，再返回支付"))
                            else -> listener.getFunction().onFailure?.invoke(SocialError(-1, "支付宝返回错误码:$resultStatus;"))
                        }
                    } else {
                        listener.getFunction().onFailure?.invoke(SocialError(SocialError.CODE_PAY_RESULT_ERROR))
                    }
                }
            }catch (e: Exception){
                listener.getFunction().printLog?.invoke("AliPlatform::支付错误:${e}")
            }
        }.start()
//        SocialGo.getExecutor().execute {
//        }
    }

    override fun login(activity: Activity, listener: OnLoginListener) {
        val authTask = AuthTask(activity)
        SocialGo.getExecutor().execute {
            val result = authTask.authV2(mParams, true)
            Log.i("tdxtxt:AliPlatform","支付宝登录授权结果:$result")
            listener.getFunction().printLog?.invoke("AliPlatform::支付宝登录授权结果:$result")

            val code = result.get("resultStatus")
            val content = result.get("result")
            val msg = result.get("memo")
            val values = content?.split("&")

            var alipayOpenId: String? = ""
            var authToken: String? = ""
            var authResultCode: String? = ""

            values?.forEach { value ->
                if(value.startsWith("alipay_open_id")){
                    alipayOpenId =
                        value.replace("alipay_open_id=", "").run {
                            var temp = this
                            if(temp.startsWith("\"")) temp = temp.replaceFirst("\"", "")
                            if(temp.endsWith("\"")) temp = temp.replaceFirst("\"", "")
                            temp
                        }
                }else if(value.startsWith("auth_code")){
                    authToken =
                            value.replace("auth_code=", "").run {
                                var temp = this
                                if(temp.startsWith("\"")) temp = temp.replaceFirst("\"", "")
                                if(temp.endsWith("\"")) temp = temp.replaceFirst("\"", "")
                                temp
                            }
                }else if(value.startsWith("result_code")){
                    authResultCode =
                            value.replace("result_code=", "").run {
                                var temp = this
                                if(temp.startsWith("\"")) temp = temp.replaceFirst("\"", "")
                                if(temp.endsWith("\"")) temp = temp.replaceFirst("\"", "")
                                temp
                            }
                }
            }
            //判断resultCode 为“9000”且result_code为“200”则代表授权成功，
            if(code == "9000" && authResultCode == "200"){
                val loginResult = LoginResult(Target.LOGIN_ALI, AliUser(), AliAccessToken().apply {
                    access_token = authToken
                    openid = alipayOpenId
                })
                listener.getFunction().onLoginSuccess?.invoke(loginResult)
            }else if(TextUtils.isEmpty(authResultCode)){
                listener.getFunction().onCancel?.invoke()
            }else {
                listener.getFunction().onFailure?.invoke(SocialError(-1, "code=${code};authResultCode=${authResultCode};msg=${msg}"))
            }
        }
    }
}
