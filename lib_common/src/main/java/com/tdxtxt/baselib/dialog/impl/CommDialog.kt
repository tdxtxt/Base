package com.tdxtxt.baselib.dialog.impl

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.dialog.IBDialog
import com.tdxtxt.baselib.R

/**
 * @作者： tangdx
 * @创建时间： 2018\4\17 0017
 * @功能描述： 提示框
 * @传入参数说明： 无
 * @返回参数说明： 无
 */
class CommDialog(context: FragmentActivity) : CenterBaseDialog(context) {
    private var autoDismiss = true

    var tvTitle: TextView? = null
    var tvContent: TextView? = null
    var tvBtnLeft: TextView? = null
    var tvBtnCenter: TextView? = null
    var tvBtnRight: TextView? = null

    /**
     * 设置是否点击按钮会自动关闭弹框
     */
    fun setAutoDismiss(autoDismiss: Boolean): CommDialog {
        this.autoDismiss = autoDismiss
        return this
    }

    var title: String? = null
    fun setTitle(title: String?): CommDialog {
        this.title = title
        if(TextUtils.isEmpty(title)){
            tvTitle?.visibility = View.GONE
        }else{
            tvTitle?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
        return this
    }

    var content: String? = null
    fun setContent(content: String?): CommDialog {
        this.content = content
        if(TextUtils.isEmpty(content)){
            tvContent?.visibility = View.GONE
        }else{
            tvContent?.visibility = View.VISIBLE
            tvContent?.text = content
        }
        return this
    }

    var leftMenu: (MenuCallBack.() -> Unit)? = null
    fun setLeftMenu(listener: (MenuCallBack.() -> Unit)?): CommDialog {
        this.leftMenu = listener
        if (listener != null) {
            val callback = object : MenuCallBack() {}
            callback.listener()

            tvBtnLeft?.visibility = View.VISIBLE
            tvBtnLeft?.text = callback.menuText
            tvBtnLeft?.setOnClickListener {
                callback.click?.invoke()
                if (autoDismiss) dismiss()
            }
        } else {
            tvBtnLeft?.visibility = View.GONE
        }
        changeListener()
        return this
    }

    var centerMenu: (MenuCallBack.() -> Unit)? = null
    fun setCenterMenu(listener: (MenuCallBack.() -> Unit)?): CommDialog {
        this.centerMenu = listener
        if (listener != null) {
            val callback = object : MenuCallBack() {}
            callback.listener()

            tvBtnCenter?.visibility = View.VISIBLE
            tvBtnCenter?.text = callback.menuText
            tvBtnCenter?.setOnClickListener {
                callback.click?.invoke()
                if (autoDismiss) dismiss()
            }
        } else {
            tvBtnCenter?.visibility = View.GONE
        }
        changeListener()
        return this
    }

    var rightMenu: (MenuCallBack.() -> Unit)? = null
    fun setRightMenu(listener: (MenuCallBack.() -> Unit)?): CommDialog {
        this.rightMenu = listener
        if (listener != null) {
            val callback = object : MenuCallBack() {}
            callback.listener()

            tvBtnRight?.visibility = View.VISIBLE
            tvBtnRight?.text = callback.menuText
            tvBtnRight?.setOnClickListener {
                callback.click?.invoke()
                if (autoDismiss) dismiss()
            }
        } else {
            tvBtnRight?.visibility = View.GONE
        }
        changeListener()
        return this
    }

    private fun changeListener() {
        if (tvBtnLeft?.visibility == View.GONE &&
                tvBtnCenter?.visibility == View.GONE &&
                tvBtnRight?.visibility == View.GONE) {
            findViewById<View>(R.id.view_line)?.visibility = View.GONE
            findViewById<View>(R.id.layout_menu)?.visibility = View.GONE
        } else {
            findViewById<View>(R.id.view_line)?.visibility = View.VISIBLE
            findViewById<View>(R.id.layout_menu)?.visibility = View.VISIBLE
        }
    }

    override fun getLayoutId() = R.layout.baselib_dialog_commtips_view

    override fun onCreate(dialog: IBDialog) {
        tvTitle = findViewById(R.id.tv_common_prompt_title)
        tvContent = findViewById(R.id.tv_common_prompt_content)
        tvBtnLeft = findViewById(R.id.btn_common_prompt_left)
        tvBtnCenter = findViewById(R.id.btn_common_prompt_center)
        tvBtnRight = findViewById(R.id.btn_common_prompt_right)

        setTitle(title)
        setContent(content)
        setLeftMenu(leftMenu)
        setRightMenu(rightMenu)
        setCenterMenu(centerMenu)
    }

}