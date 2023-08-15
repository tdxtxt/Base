package com.tdxtxt.social.alipay.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.alipay.sdk.app.PayTask
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.utils.ThreadUtils
import java.lang.Exception

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class AliPayHelper : Recyclable {

    fun doPay(context: Context?, params: String?, listener: OnPayListener?, complete: (() -> Unit)?){
        listener?.printLog("支付宝支付开始参数:$params")
        Thread{
            try{
                val payTask = PayTask(context as Activity)
                listener?.printLog("支付宝版本:${payTask.version}")
                val payResult = payTask.payV2(params, true)
                listener?.printLog("支付宝支付结果:$payResult")
                ThreadUtils.runOnUiThread {
                    if (payResult == null){
                        listener?.onFailure("支付失败")
                        complete?.invoke()
                        return@runOnUiThread
                    }

                    val resultStatus = payResult["resultStatus"]
                    when {
                        TextUtils.equals(resultStatus, "9000") -> //支付成功
                            listener?.onSuccess()
                        TextUtils.equals(resultStatus, "8000") -> //支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            listener?.onFailure("请等待支付结果的确认，最终交易是否成功以服务端异步通知为准")
                        TextUtils.equals(resultStatus, "6001") -> //支付取消
                            listener?.onCancel()
                        TextUtils.equals(resultStatus, "6002") -> //网络连接出错
                            listener?.onFailure("网络连接错误")
                        TextUtils.equals(resultStatus, "4000") -> //支付错误
                            listener?.onFailure("支付错误 4000")
                        TextUtils.equals(resultStatus, "7001") -> //待支付订单存在
                            listener?.onFailure("已有支付界面，请前先前往支付宝应用取消存在支付界面，再返回支付")
                        TextUtils.equals(resultStatus, "5000") -> //待支付订单存在
                            listener?.onFailure("已有支付界面，请前先前往支付宝应用取消存在支付界面，再返回支付")
                        else -> listener?.onFailure("支付宝返回错误码:$resultStatus;")
                    }
                    complete?.invoke()
                }
            }catch (ex: Exception){
                listener?.onFailure("支付失败")
                complete?.invoke()
                ex.printStackTrace()
            }
        }.start()
    }

    override fun onDestory() {

    }
}