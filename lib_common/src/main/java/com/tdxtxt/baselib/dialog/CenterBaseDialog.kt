package com.tdxtxt.baselib.dialog

import android.app.Activity
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.util.XPopupUtils
import com.tdxtxt.baselib.tools.lifecycleOwner
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.baselib.ui.viewbinding.ViewBindingWrapper

/**
 * 功能描述:
 * @author tangdexiang
 * @since 2020/7/27
 */
abstract class CenterBaseDialog constructor(val context: FragmentActivity?) : IBDialog {
    internal val viewbindingWrapper by lazy { ViewBindingWrapper<ViewBinding>() }
    val dialog: CenterPopupView? by lazy {
        if(context == null) null else
        object : CenterPopupView(context){
            override fun getImplLayoutId() = getLayoutId()
            override fun getPopupWidth(): Int {
                return getDialogWidth()
            }
            override fun getPopupHeight(): Int {
                return getDialogHeight()
            }
            override fun onCreate() {
                if(this@CenterBaseDialog is IViewBinding<*>){
                    setViewBindingRoot(popupImplView)
                }
                onCreate(this@CenterBaseDialog)
            }
        }
    }
    val builder: XPopup.Builder? by lazy {
        if(context == null) null else
        XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
            override fun onBackPressed(popupView: BasePopupView?) = false  //如果你自己想拦截返回按键事件，则重写这个方法，返回true即可

            override fun onDismiss(popupView: BasePopupView?) {
                mCancelListener?.invoke()
                if(this@CenterBaseDialog is IViewBinding<*>){
                    dismiss()
                }
            }
            override fun beforeShow(popupView: BasePopupView?) {
            }
            override fun onCreated(popupView: BasePopupView?) {
            }
            override fun beforeDismiss(popupView: BasePopupView?) {
            }
            override fun onShow(popupView: BasePopupView?) {
            }
        }).enableDrag(false).maxWidth(getDialogMaxWidth())
    }
    var popupView: BasePopupView? = null

    override fun isShow(): Boolean {
        return dialog?.isShow?: false
    }

    override fun show(): CenterBaseDialog {
        popupView?.dismiss()
        return builder?.asCustom(dialog).apply { popupView = this }?.show()?.lifecycleOwner(context).run { this@CenterBaseDialog }
    }

    override fun dismiss() {
        popupView?.dismiss()
    }

    override fun hide() {
        popupView?.dismiss()
    }

    override fun getActivity(): Activity? {
        if (context != null && context is Activity) return context
        var bindAct: Activity? = null
        var context = dialog?.context
        do {
            if (context is Activity) {
                bindAct = context
                break
            } else if (context is ContextThemeWrapper) {
                context = context.baseContext
            } else {
                break
            }
        } while (true)
        return bindAct
    }

    override fun getRootView() = dialog?.rootView

    var mCancelListener: (() -> Unit)? = null
    override fun setCancelListener(cancelListener: () -> Unit): CenterBaseDialog {
        mCancelListener = cancelListener
        return this
    }

    var mCancelable = true
    override fun setCancelable(cancelable: Boolean): CenterBaseDialog {
        mCancelable = cancelable
        builder?.dismissOnTouchOutside(if(mCancelable) mCancelableOnTouchOutside else false)
        builder?.dismissOnBackPressed(mCancelable)
        return this
    }

    var mCancelableOnTouchOutside = true
    override fun setCancelableOnTouchOutside(cancelableOnTouchOutside: Boolean): CenterBaseDialog {
        mCancelableOnTouchOutside = cancelableOnTouchOutside
        builder?.dismissOnTouchOutside(if(mCancelable) cancelableOnTouchOutside else false)
        return this
    }

    override fun <T : View> findViewById(@IdRes id: Int): T? {
        return dialog?.findViewById(id)
    }

    override fun getDialogWidth() = 0

    override fun getDialogHeight() = 0

    override fun getDialogMaxWidth(): Int {
        return ((Math.min(XPopupUtils.getAppWidth(context), XPopupUtils.getAppHeight(context))) * 0.8f).toInt()
    }
}