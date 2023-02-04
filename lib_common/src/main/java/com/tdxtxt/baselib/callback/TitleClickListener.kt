package com.tdxtxt.baselib.callback

import android.view.View

abstract class TitleClickListener {
    fun onLeftClick(v: View){}
    fun onTitleClick(v: View){}
    fun onRightClick(v: View){}
    fun initCustomClick(v: View){}
}