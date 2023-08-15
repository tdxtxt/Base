package com.tdxtxt.social.alipay.utils

import android.app.Activity
import android.text.TextUtils
import com.alipay.sdk.app.AuthTask
import com.tdxtxt.social.core.bean.AuthInfo
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.Recyclable

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class AliLoginHelper : Recyclable {

    /**
     * 开始登录
     */
    fun login(activity: Activity?, params: String?, listener: OnLoginListener?, complete: (() -> Unit)?) {
        val authTask = AuthTask(activity)
        Thread{
            val result = authTask.authV2(params, true)
            listener?.printLog("支付宝登录授权结果:$result")
            val code = result["resultStatus"]
            val content = result["result"]
            val msg = result["memo"]
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
                val result = AuthInfo()
                result.aliAccessToken = authToken
                result.aliOpenId = alipayOpenId
                listener?.onSuccess(result)
            }/*else if(TextUtils.isEmpty(authResultCode)){
                listener?.onCancel()
            }*/else {
                listener?.onFailure("code=${code};authResultCode=${authResultCode};msg=${msg}")
            }
            complete?.invoke()
        }.start()
    }

    override fun onDestory() {

    }
}