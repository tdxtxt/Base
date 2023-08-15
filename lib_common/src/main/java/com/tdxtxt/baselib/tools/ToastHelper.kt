package com.tdxtxt.baselib.tools

import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.tdxtxt.baselib.dialog.impl.XToast
import kotlin.math.abs

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   :
 * </pre>
 */
object ToastHelper {
    private var mLastMessage: String? = null
    private var mLastTimeMillis = 0L

    init {
        val config = XToast.Config.get()
            .setGravity(Gravity.CENTER)
            .setAlpha(200)
            .allowQueue(false);
    }

    @JvmStatic
    fun showToast(msg: String?){
        if(msg == null) return
        show(msg)
    }

    @JvmStatic
    fun showToast(stringRes: Int){
        val message = Utils.getApp().resources.getString(stringRes)
        show(message)
    }

    private fun show(message: String){
        if(message == mLastMessage){
            if(abs(SystemClock.uptimeMillis() - mLastTimeMillis) < 1000)
                return
        }
        mLastMessage = message
        mLastTimeMillis = SystemClock.uptimeMillis()
        val runnable = { XToast.normal(Utils.getApp(), message).show() }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.invoke()
        } else {
            ThreadUtils.runOnUiThreadDelayed(runnable, 100)
        }
    }
}