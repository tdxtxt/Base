package com.tdxtxt.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.LayoutAppTreeviewBinding
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-09-20
 *     desc   :
 * </pre>
 */
class TreeView: LinearLayout, IViewBinding<LayoutAppTreeviewBinding> {
    constructor(context: Context): super(context) { initView(context) }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) { initView(context) }

    private fun initView(context: Context){
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.layout_app_treeview, this, true)
        setViewBindingRoot(this)

        viewbindingOrNull()?.tv1?.text = "TextView1"
        viewbindingOrNull()?.tv2?.text = "TextView2"
    }
    override fun view2Binding(rootView: View) = LayoutAppTreeviewBinding.bind(rootView)

}