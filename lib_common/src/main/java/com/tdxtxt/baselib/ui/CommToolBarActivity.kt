package com.tdxtxt.baselib.ui

import android.os.Bundle
import android.view.View
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.view.titlebar.OnTitleBarListener
import com.tdxtxt.baselib.view.titlebar.TitleBar


abstract class CommToolBarActivity : BaseActivity() {
    private var mTitleBar: TitleBar? = null
    private var realMenuCallBack: MenuCallBack? = null
//    private var titleListener: TitleClickListener? = null

    /**
     * 布局中TitleBar控件id默认R.id.titlebar，若自定义id，需要重写此方法
     */
    open fun getToolBarResId() = R.id.titlebar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTitleBar()
    }

    private fun initTitleBar() {
        getTitleBar()?.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View) {
                if(clickTitleBarBack()){
                    interceptCallBack?.invoke()
                    if (!interceptBackEvent) finish()
                }
            }
            override fun onTitleClick(v: View) {
            }
            override fun onRightClick(v: View) {
                realMenuCallBack?.click?.invoke()
            }
        })
    }

    open fun clickTitleBarBack(): Boolean = true

    /**
     * 获取TitleBar控件
     */
    fun getTitleBar(): TitleBar? {
        if(mTitleBar == null) mTitleBar = findViewById(getToolBarResId())
        return mTitleBar
    }

    open fun setTitleBar(title: String?, rightMenu: (MenuCallBack.() -> Unit)? = null) {
        getTitleBar()?.apply {
            this.title = title
            rightMenu?.apply {
                realMenuCallBack = object : MenuCallBack(){ }
                realMenuCallBack?.apply {
                    rightMenu()
                    if(isTextMenu()) setRightTitle(menuText)
                    else if(isIconMenu()) setRightIcon(icon)
                }
            }
        }
    }


     override fun initStatusBar() {

    }



}