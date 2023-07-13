package com.tdxtxt.baselib.dialog.impl

import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.lxj.xpopup.enums.PopupAnimation
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.dialog.IBDialog
import com.tdxtxt.baselib.tools.lifecycleOwner

/**
 * @作者： 唐德祥
 * @创建时间： 2018\4\17 0017
 * @功能描述： 菊花转
 * @传入参数说明： 无
 * @返回参数说明： 无
 */
class ProgressDialog(context : FragmentActivity) : CenterBaseDialog(context) {
    private var tvDesc: TextView? = null

    fun setDesc(desc: String?): ProgressDialog = tvDesc?.run { text = desc?: ""; this@ProgressDialog }?: this

    override fun getLayoutId() = layoutResId

    override fun onCreate(dialog: IBDialog) {
        tvDesc = dialog.findViewById(R.id.tv_desc)
        setDesc("加载中...")
    }

    override fun show(): CenterBaseDialog {
        popupView?.dismiss()
        return builder
            .hasShadowBg(false)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .isLightStatusBar(true)
            .isLightNavigationBar(true)
            .asCustom(dialog).apply { popupView = this }.show().lifecycleOwner(context)
            .run { this@ProgressDialog }
    }

    companion object{
        var mGlobalCancelable = true
        var mGlobalOutsideCancelable = true
        private var layoutResId = R.layout.baselib_dialog_commprogress_view
        @JvmStatic
        fun setLayoutId(resId: Int){
            if(resId > 0){
                layoutResId = resId
            }
        }

        @JvmStatic
        fun setGlobalCancel(cancelable: Boolean, outsideCancelable: Boolean){
            this.mGlobalCancelable = cancelable
            this.mGlobalOutsideCancelable = outsideCancelable
        }

        @JvmStatic
        fun createProgressDialog(activity: FragmentActivity, desc: String = "加载中...", cancelable: Boolean? = null, outsideCancelable: Boolean? = null): ProgressDialog {
            return ProgressDialog(activity).apply { setDesc(desc).setCancelable(cancelable?: mGlobalCancelable).setCancelableOnTouchOutside(outsideCancelable?: mGlobalOutsideCancelable) }
        }
    }
}
