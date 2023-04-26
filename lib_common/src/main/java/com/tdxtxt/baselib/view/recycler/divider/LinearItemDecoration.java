package com.tdxtxt.baselib.view.recycler.divider;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

/**
 * @Author donkingliang QQ:1043214265 github:https://github.com/donkingliang
 * @Description 通用的LinearItemDecoration类，用于设置LinearLayoutManager的RecyclerView间隔装饰
 * @Date 2020/6/19
 */
public class LinearItemDecoration extends LinearVariedItemDecoration {

    private Drawable mDivider;

    private int mDividerSize;

    public LinearItemDecoration(float dividerSize, @ColorInt int color) {
        this(dividerSize, new ColorDrawable(color));
    }

    public LinearItemDecoration(float dividerSize, Drawable divider) {
        mDividerSize = (int) DividerUtils.dp2px(dividerSize);
        mDivider = divider;
    }

    public LinearItemDecoration(float dividerSize) {
        this(dividerSize, null);
    }

    @Override
    public int getDividerSize(int position) {
        return mDividerSize;
    }

    @Override
    public Drawable getDivider(int position) {
        return mDivider;
    }

}
