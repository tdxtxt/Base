package com.tdxtxt.base.fragment

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.FragmentTablayoutY2Binding
import com.tdxtxt.baselib.ui.BaseFragment
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-23
 *     desc   :
 * </pre>
 */
class YTabLayoutFragment2 : BaseFragment(), IViewBinding<FragmentTablayoutY2Binding> {
    private val adapter by lazy {
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_fragment_placeholde){
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv_text, item)
            }
        }
    }
    override fun getLayoutId() = R.layout.fragment_tablayout_y2
    override fun viewbind(rootView: View): FragmentTablayoutY2Binding {
        return FragmentTablayoutY2Binding.bind(rootView)
    }
    override fun initUi() {
        viewbinding().recyclerView.adapter = adapter
        adapter.setList(mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"))
        viewbinding().tabLayout.setRecyclerView(viewbinding().recyclerView, mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"))
    }
}