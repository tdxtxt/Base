package com.tdxtxt.baselib.dialog

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes

/**
 * 功能描述:
 * @author tangdexiang
 * @since 2020/7/27
 */
interface IBDialog {
    fun getLayoutId(): Int
    fun onCreate(dialog: IBDialog)
    fun isShow(): Boolean
    fun show(): IBDialog
    fun dismiss()
    fun hide()
    fun getActivity(): Activity?
    fun getRootView(): View?

    fun setCancelListener(cancelListener: () -> Unit): IBDialog
    fun setCancelable(cancelable: Boolean): IBDialog
    fun setCancelableOnTouchOutside(cancelableOnTouchOutside: Boolean): IBDialog

    fun <T : View> findViewById(@IdRes int: Int): T?

    fun getDialogWidth(): Int
    fun getDialogHeight(): Int
    fun getDialogMaxWidth(): Int



}