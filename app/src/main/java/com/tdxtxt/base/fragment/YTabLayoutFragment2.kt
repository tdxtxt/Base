package com.tdxtxt.base.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tdxtxt.base.R
import com.tdxtxt.baselib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_tablayout_y.tabLayout
import kotlinx.android.synthetic.main.fragment_tablayout_y2.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-23
 *     desc   :
 * </pre>
 */
class YTabLayoutFragment2 : BaseFragment() {
    private val adapter by lazy {
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_fragment_placeholde){
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv_text, item)
            }
        }
    }
    override fun getLayoutId() = R.layout.fragment_tablayout_y2

    override fun initUi() {
        recyclerView.adapter = adapter
        adapter.setList(mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"))
        tabLayout.setRecyclerView(recyclerView, mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"))
    }
}