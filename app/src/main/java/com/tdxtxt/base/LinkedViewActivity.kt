package com.tdxtxt.base

import com.tdxtxt.baselib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_linked.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/14
 *     desc   :
 * </pre>
 */
class LinkedViewActivity : BaseActivity() {
    override fun getLayoutResId() = R.layout.activity_linked

    override fun initUi() {
        pickerView.setData(mutableListOf())
    }
}