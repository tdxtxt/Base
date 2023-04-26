package com.tdxtxt.baselib.view.recycler.divider;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

/**
 * @Author donkingliang QQ:1043214265 github:https://github.com/donkingliang
 * @Description 通用的GridItemDecoration类，用于设置GridLayoutManager的RecyclerView间隔装饰
 * @Date 2020/6/19
 */
public class GridItemDecoration extends GridVariedItemDecoration {

    private Drawable mRowDivider;
    private Drawable mColumnDivider;

    private int mRowDividerSize;
    private int mColumnDividerSize;


    public GridItemDecoration(float rowDividerSize, Drawable rowDivider, float columnDividerSize, Drawable columnDivider) {
        mRowDividerSize = (int) DividerUtils.dp2px(rowDividerSize);
        mRowDivider = rowDivider;
        mColumnDividerSize = (int) DividerUtils.dp2px(columnDividerSize);
        mColumnDivider = columnDivider;
    }

    public GridItemDecoration(float rowDividerSize, @ColorInt int rowColor, float columnDividerSize, @ColorInt int columnColor) {
        this(rowDividerSize, new ColorDrawable(rowColor), columnDividerSize, new ColorDrawable(columnColor));
    }

    public GridItemDecoration(float rowDividerSize, float columnDividerSize, @ColorInt int color) {
        this(rowDividerSize, new ColorDrawable(color), columnDividerSize, new ColorDrawable(color));
    }

    public GridItemDecoration(float rowDividerSize, float columnDividerSize){
        this(rowDividerSize, null, columnDividerSize, null);
    }

    @Override
    public int getRowDividerSize(int position) {
        return mRowDividerSize;
    }

    @Override
    public Drawable getRowDivider(int position) {
        return mRowDivider;
    }

    @Override
    public int getColumnDividerSize(int position) {
        return mColumnDividerSize;
    }

    @Override
    public Drawable getColumnDivider(int position) {
        return mColumnDivider;
    }
}
