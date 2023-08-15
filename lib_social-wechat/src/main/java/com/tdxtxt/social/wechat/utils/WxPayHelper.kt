package com.tdxtxt.social.wechat.utils

import android.util.Log
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import org.json.JSONObject
import java.lang.Exception

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
class WxPayHelper constructor(private val wxApi: IWXAPI): Recyclable {
    private var mListener: OnPayListener? = null
    fun doPay(params: String?, listener: OnPayListener?, complete: (() -> Unit)?){
        if(wxApi.wxAppSupportAPI < Build.PAY_SUPPORTED_SDK_INT){
            listener?.onFailure("android最低版本不支持")
            return
        }
        try{
            val json = JSONObject(params)
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
            val sendReq = wxApi.sendReq(req)
            listener?.printLog("微信支付请求：checkArgs=$check;sendReq=$sendReq")
        }catch (e: Exception){
            e.printStackTrace()
            listener?.onFailure("支付参数解析错误")
            complete?.invoke()
        }
    }

    fun getListener() = mListener
    override fun onDestory() {
        mListener = null
    }
}