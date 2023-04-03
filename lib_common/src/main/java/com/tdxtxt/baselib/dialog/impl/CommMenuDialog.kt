package com.tdxtxt.baselib.dialog.impl

import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.dialog.BottomBaseDialog
import com.tdxtxt.baselib.dialog.IBDialog
import com.tdxtxt.baselib.view.recycler.divider.BaseItemDecoration
import com.tdxtxt.baselib.view.recycler.divider.LinerItemDecoration

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/30
 *     desc   : 菜单点击
 * </pre>
 */
class CommMenuDialog constructor(activity: FragmentActivity): BottomBaseDialog(activity){
    var mAdapter: BaseQuickAdapter<MenuCallBack, BaseViewHolder>? = null
    var mData: MutableList<MenuCallBack>? = null

    override fun getLayoutId() = R.layout.baselib_dialog_menu_view

    override fun onCreate(dialog: IBDialog) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        findViewById<View>(R.id.iv_close)?.setOnClickListener {
            dismiss()
        }

        recyclerView?.addItemDecoration(
            LinerItemDecoration.Builder(context, OrientationHelper.VERTICAL)
                .setDividerMarginHorizontal(16f)
                .setDividerColor(Color.parseColor("#EBECF0"))
                .build()
        )
        mAdapter = object : BaseQuickAdapter<MenuCallBack, BaseViewHolder>(R.layout.baselib_item_menu_dialog_text){
            override fun convert(holder: BaseViewHolder, item: MenuCallBack) {
                holder.setText(R.id.tv_menu, item.menuText)
            }
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item = mAdapter?.getItem(position)
            item?.click?.invoke()
            dismiss()
        }
        recyclerView?.adapter = mAdapter

        mAdapter?.setList(mData)
    }

    fun addMenu(menu: MenuCallBack): CommMenuDialog{
        if(mData == null) mData = mutableListOf()
        mData?.add(menu)
        return this
    }
}