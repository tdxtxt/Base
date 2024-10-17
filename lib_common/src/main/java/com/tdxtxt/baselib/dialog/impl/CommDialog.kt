package com.tdxtxt.baselib.dialog.impl

import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.StringUtils
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
    var viewLine: View? = null
    var layoutMenu: View? = null

    /**
     * 设置是否点击按钮会自动关闭弹框
     */
    fun setAutoDismiss(autoDismiss: Boolean): CommDialog {
        this.autoDismiss = autoDismiss
        return this
    }

    var title: CharSequence? = null
    fun setTitle(title: CharSequence?): CommDialog {
        this.title = title
        if(TextUtils.isEmpty(title)){
            tvTitle?.visibility = View.GONE
        }else{
            tvTitle?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
        return this
    }

    var content: CharSequence? = null
    fun setContent(content: CharSequence?): CommDialog {
        this.content = content
        if(TextUtils.isEmpty(content)){
            tvContent?.visibility = View.GONE
        }else{
            tvContent?.visibility = View.VISIBLE
            tvContent?.text = content
        }
        return this
    }

    var leftMenu: MenuCallBack? = null
    fun setLeftMenu(listener: MenuCallBack?): CommDialog {
        this.leftMenu = listener
        if (leftMenu != null) {

            tvBtnLeft?.visibility = View.VISIBLE
            tvBtnLeft?.text = leftMenu?.menuText
            tvBtnLeft?.setOnClickListener {
                leftMenu?.click?.invoke()
                if (autoDismiss) dismiss()
            }
        } else {
            tvBtnLeft?.visibility = View.GONE
        }
        changeListener()
        return this
    }

    var centerMenu: MenuCallBack? = null
    fun setCenterMenu(listener: MenuCallBack?): CommDialog {
        this.centerMenu = listener
        if (listener != null) {

            tvBtnCenter?.visibility = View.VISIBLE
            tvBtnCenter?.text = centerMenu?.menuText
            tvBtnCenter?.setOnClickListener {
                centerMenu?.click?.invoke()
                if (autoDismiss) dismiss()
            }
        } else {
            tvBtnCenter?.visibility = View.GONE
        }
        changeListener()
        return this
    }

    var rightMenu: MenuCallBack? = null
    fun setRightMenu(listener: MenuCallBack?): CommDialog {
        this.rightMenu = listener
        if (listener != null) {

            tvBtnRight?.visibility = View.VISIBLE
            tvBtnRight?.text = rightMenu?.menuText
            tvBtnRight?.setOnClickListener {
                rightMenu?.click?.invoke()
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
            tvBtnRight?.visibility == View.GONE
        ) {
            viewLine?.visibility = View.GONE
            layoutMenu?.visibility = View.GONE
        } else {
            viewLine?.visibility = View.VISIBLE
            layoutMenu?.visibility = View.VISIBLE
        }
    }

    override fun getLayoutId() = layoutResId

    override fun onCreate(dialog: IBDialog) {
        tvTitle = findViewById(R.id.tv_common_prompt_title)
        tvContent = findViewById(R.id.tv_common_prompt_content)
        tvBtnLeft = findViewById(R.id.btn_common_prompt_left)
        tvBtnCenter = findViewById(R.id.btn_common_prompt_center)
        tvBtnRight = findViewById(R.id.btn_common_prompt_right)
        viewLine = findViewById(R.id.view_line)
        layoutMenu = findViewById(R.id.layout_menu)

        //超过maxHeight支持滑动
        tvContent?.movementMethod = ScrollingMovementMethod.getInstance()

        setTitle(title)
        setContent(content)
        setLeftMenu(leftMenu)
        setRightMenu(rightMenu)
        setCenterMenu(centerMenu)
    }

    companion object{
        private var layoutResId = R.layout.baselib_dialog_commtips_view
        @JvmStatic
        fun setLayoutId(resId: Int){
            if(resId > 0){
                layoutResId = resId
            }
        }
        @JvmStatic
        fun showTips(context: FragmentActivity, content: CharSequence?){
            showCommDialog(context,  context.getString(R.string.温馨提示), content)
        }
        @JvmStatic
        fun showCommDialog(activity: FragmentActivity?, content: CharSequence?,
                           leftMenu: MenuCallBack? = null, rightMenu: MenuCallBack? = null): CommDialog? {
            if (activity == null) return null
            return createCommDialog(activity,  activity.getString(R.string.温馨提示), content, leftMenu = leftMenu, rightMenu = rightMenu)?.apply { show() }
        }
        @JvmStatic
        fun showCommDialog(activity: FragmentActivity?, title: CharSequence?, content: CharSequence?,
                           leftMenu: MenuCallBack? = null, rightMenu: MenuCallBack? = null): CommDialog? {
            if (activity == null) return null
            return createCommDialog(activity, title, content, leftMenu = leftMenu, rightMenu = rightMenu)?.apply { show() }
        }
        @JvmStatic
        fun showCommDialog(activity: FragmentActivity?, title: CharSequence?, content: CharSequence?, centerMenu: MenuCallBack? = null): CommDialog? {
            if (activity == null) return null
            return createCommDialog(activity, title, content, centerMenu = centerMenu)?.apply { show() }
        }
        @JvmStatic
        fun createCommDialog(activity: FragmentActivity?, title: CharSequence? = activity?.getString(R.string.温馨提示), content: CharSequence?,
                             leftMenu: MenuCallBack? = null,
                             centerMenu: MenuCallBack? = null,
                             rightMenu: MenuCallBack? = null): CommDialog? {
            if (activity == null) return null
            return CommDialog(activity).setTitle(title)
                .setContent(content)
                .setLeftMenu(leftMenu)
                .setCenterMenu(centerMenu)
                .setRightMenu(rightMenu)
        }
    }
}