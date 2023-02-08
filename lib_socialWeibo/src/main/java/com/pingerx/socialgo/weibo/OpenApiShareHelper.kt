package com.pingerx.socialgo.weibo

import android.app.Activity
import android.text.TextUtils
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.exception.SocialError
import com.pingerx.socialgo.core.listener.OnShareListener
import com.pingerx.socialgo.core.model.ShareEntity
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.common.UiError
import org.json.JSONObject
import java.util.*

/**
 * openApi 分享动图
 */
class OpenApiShareHelper constructor(private val loginHelper: WbLoginHelper, private val listener: OnShareListener?) {

    fun post(activity: Activity, obj: ShareEntity) {
        loginHelper.justAuth(activity, object : WbAuthListenerImpl() {
            override fun onComplete(authToken: Oauth2AccessToken?) {
                SocialGo.getExecutor().execute {
                    val params = HashMap<String, String>()
                    params["access_token"] = authToken?.getAccessToken()?: ""
                    params["status"] = obj.getSummary()
                    val data = SocialGo.getRequestAdapter().postData("https://api.weibo.com/2/statuses/share.json", params, "pic", obj.getThumbImagePath())
                    SocialGo.getHandler().post {
                        if (TextUtils.isEmpty(data)) {
                            listener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PARSE_ERROR, "open api 分享失败 $data"))
                        } else {
                            val jsonObject = JSONObject(data)
                            if (jsonObject.has("id") && jsonObject.get("id") != null) {
                                listener?.getFunction()?.onSuccess?.invoke()
                            } else {
                                listener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_PARSE_ERROR, "open api 分享失败 $data"))
                            }
                        }
                    }
                }
            }
        })
    }

    open inner class WbAuthListenerImpl : WbAuthListener {
        override fun onComplete(authToken: Oauth2AccessToken?){}

        override fun onError(msg: UiError?){
            listener?.getFunction()?.onFailure?.invoke(SocialError(SocialError.CODE_SDK_ERROR, "#WbAuthListenerImpl#wb auth fail," + msg?.errorCode + " " + msg?.errorMessage))
        }

        override fun onCancel(){
            listener?.getFunction()?.onCancel?.invoke()
        }
    }

}
