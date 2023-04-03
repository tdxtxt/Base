package com.tdxtxt.baselib.view.recycler.divider

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Date:2019/7/10
 * Author:kunkun.wang
 * Des:线性布局分割线 注意该分割线 只能用在LinerLayoutManager 网格布局请使用网格分割线
 * 使用：
        可以绘制的分割线
     recyclerView.addItemDecoration(
            LinerItemDecoration.Builder(this, OrientationHelper.VERTICAL)
            .setDividerWidthPx(20)//分割线的宽度 单位px
            .setDividerMarginPx(10, 10, 10, 10)//设置分割线距离item的间隔
            .setDividerDrawByChild(true)//设置绘制分割线的长度是否是根据item的长度来绘制 默认为false代表绘制是根据RecyclerView的长度来的
            .showLastDivider(false)//最后一个item后面是否有分割线 默认为false
            .setDividerColorProvider(
            object : BaseItemDecoration.DividerColorProvider {
                override fun getDividerColor(position: Int, parent: RecyclerView): Int {
                    when ((position + 1) % 4) {
                        0 -> {
                    return Color.parseColor("#FF0000")
                        }
                    1 -> {
                        return Color.parseColor("#00FF00")
                        }
                    2 -> {
                        return Color.parseColor("#0000FF")
                        }
                    3 -> {
                        return Color.parseColor("#000000")
                        }
                    else -> {
                        return Color.parseColor("#000000")
                        }
                    }
                }
            })//设置分割线绘制的颜色  我们可以设置在不同的位置绘制不同的颜色
            .setDividerVisibleProvider(
                object : BaseItemDecoration.DividerVisibleProvider {
                    override fun shouldHideDivider(position: Int, parent: RecyclerView): Boolean {
                        //在3的倍数位置 不显示颜色
                        return (position + 1) % 3 == 0
                    }
                })//设置在某个位置隐藏分割线 但是分割线的间隔还是在的,只是不再绘制而已
            .build())

        空格分割线
    recyclerView.addItemDecoration(
        LinerItemDecoration.Builder(this, OrientationHelper.VERTICAL)
        .setDividerWidthPx(20)
        .showLastDivider(true)
        .showTopDivider(true)
        .setBottomDividerWidthPx(50)
        .setTopDividerWidthPx(50)
        .build()
    )
 **/
class LinerItemDecoration(builder: Builder) : BaseItemDecoration(builder) {



    private var left = 0
    private var right = 0
    private var top = 0
    private var bottom = 0
    private var decorationHeight = 0
    override fun setItemOffsets(
        position: Int,
        itemCount: Int,
        outRect: Rect,
        view: View,
        parent: RecyclerView
    ) {
        decorationHeight = dividerSpaceProvider?.getDividerSpace(position, parent)
            ?: getDrawableHeight(position, parent)
        reset()
        if (orientation == OrientationHelper.VERTICAL) {
            //纵向
            if (position == 0) {
                top = recyclerViewTopSpace
            }
            if (position == (itemCount - 1)) {
                bottom = recyclerViewBottomSpace
            }
            if (isShouldShowItemDecoration(position, itemCount)) {
                bottom += (decorationHeight + margin[1] + margin[3])
                if (isDrawFirstTopDivider && position == 0) {
                    top += (decorationHeight + margin[1] + margin[3])
                }
            }
        } else {
            //横向
            if (position == 0) {
                left = recyclerViewTopSpace
            }
            if (position == (itemCount - 1)) {
                right = recyclerViewBottomSpace
            }
            if (isShouldShowItemDecoration(position, itemCount)) {
                right += (decorationHeight + margin[0] + margin[2])
                if (isDrawFirstTopDivider && position == 0) {
                    left += (decorationHeight + margin[0] + margin[2])
                }
            }
        }
        Log.d(tag, "setItemOffsets,left=${left},right=${right},top=${top},bottom=${bottom}")
        outRect.set(left, top, right, bottom)
    }


    private fun reset() {
        left = 0
        top = 0
        right = 0
        bottom = 0
    }


    override fun getDrawRectBound(
        position: Int,
        itemCount: Int,
        view: View,
        parent: RecyclerView
    ): ArrayList<Rect> {
        val dividerHeight = getDrawableHeight(position, parent)
        val itemRectRound = Rect()
        val list = ArrayList<Rect>()
        parent.getDecoratedBoundsWithMargins(view, itemRectRound)
        val rectBound = Rect(itemRectRound)
        //设置分割线的绘制区域
        if (orientation == OrientationHelper.VERTICAL) {
            //纵向
            if (dividerDrawByChild) {
                rectBound.left = rectBound.left + margin[0]
                rectBound.right = rectBound.right - margin[2]
            } else {
                if (parent.clipToPadding) {
                    rectBound.left = parent.paddingLeft + margin[0]
                    rectBound.right = parent.width - parent.paddingRight - margin[2]
                } else {
                    rectBound.left = 0 + margin[0]
                    rectBound.right = parent.width - margin[2]
                }
            }
            rectBound.left += view.translationX.toInt()
            rectBound.bottom += (view.translationY.toInt() - margin[3])
            if (position == (itemCount - 1)) {
                rectBound.bottom -= recyclerViewBottomSpace
            }
            rectBound.top = rectBound.bottom - dividerHeight
            list.add(rectBound)
            if (position == 0 && isDrawFirstTopDivider) {
                val firstTopRect = Rect(rectBound)
                firstTopRect.top =
                    itemRectRound.top + recyclerViewTopSpace + (view.translationY.toInt() + margin[1])
                firstTopRect.bottom = firstTopRect.top + dividerHeight
                list.add(firstTopRect)
            }

        } else {
            //横向
            if (dividerDrawByChild) {
                rectBound.top = rectBound.top + margin[1]
                rectBound.bottom = rectBound.bottom - margin[3]
            } else {
                if (parent.clipToPadding) {
                    rectBound.top = parent.paddingTop + margin[1]
                    rectBound.bottom = parent.height - parent.paddingBottom - margin[3]
                } else {
                    rectBound.top = 0 + margin[1]
                    rectBound.bottom = parent.height - margin[3]
                }
            }
            rectBound.top += view.translationY.toInt()
            rectBound.bottom += view.translationY.toInt()
            rectBound.right += (view.translationX.toInt() - margin[2])
            if (position == (itemCount - 1)) {
                rectBound.right -= recyclerViewBottomSpace
            }
            rectBound.left = rectBound.right - dividerHeight
            list.add(rectBound)
            if (position == 0 && isDrawFirstTopDivider) {
                val firstTopRect = Rect(rectBound)
                firstTopRect.left =
                    itemRectRound.left + recyclerViewTopSpace + (view.translationX.toInt() +margin[0])
                firstTopRect.right = firstTopRect.left + dividerHeight
                list.add(firstTopRect)
            }

        }
        Log.d(tag, "getDrawRectBound,list=$list")
        return list
    }

    class Builder(mContext: Context, layoutOrientation: Int) :
        BaseItemDecoration.Builder(mContext, layoutOrientation) {
        override fun build(): BaseItemDecoration {
            return LinerItemDecoration(this)
        }

    }
}