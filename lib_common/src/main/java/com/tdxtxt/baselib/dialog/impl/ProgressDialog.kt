package com.tdxtxt.baselib.dialog.impl

import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.dialog.IBDialog
import com.tdxtxt.baselib.R

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
    override fun getLayoutId() = R.layout.baselib_dialog_commprogress_view

    override fun onCreate(dialog: IBDialog) {
        tvDesc = dialog.findViewById(R.id.tv_desc)
        setDesc("加载中...")
    }

}
