package com.tdxtxt.baselib.view.recycler.divider

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

object ItemDecorationHelper {

    /**
     * 获取线性的空格分割线
     */
    fun getSimpleLinerSpaceItemDecoration(
        context: Context,
        layoutOrientation: Int,
        headSpace: Int,
        footerSpace: Int,
        itemSpace: Int
    ): RecyclerView.ItemDecoration {
        return LinerItemDecoration
            .Builder(context, layoutOrientation)
            .setRecyclerViewTopSpacePx(headSpace)
            .setRecyclerViewBottomSpacePx(footerSpace)
            .setDividerWidthPx(itemSpace)
            .setIsOnlySpace(true)
            .setIgnoreFootItemCount(1)
            .build()
    }

    /**
     * 获取网格的空格分割线
     */
    fun getSimpleGridSpaceITemDecoration(
        context: Context,
        layoutOrientation: Int,
        headSpace: Int,
        footerSpace: Int,
        itemSpace: Int
    ): RecyclerView.ItemDecoration {
        return GridItemDecoration
            .Builder(context, layoutOrientation)
            .setRecyclerViewTopSpacePx(headSpace)
            .setRecyclerViewBottomSpacePx(footerSpace)
            .setDividerWidthPx(itemSpace)
            .setIsOnlySpace(true)
            .build()
    }
}