package com.tdxtxt.baselib.view.recycler.divider;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * @Author donkingliang QQ:1043214265 github:https://github.com/donkingliang
 * @Description 自定义GridItemDecoration的基类，根据position返回每个item的间隔大小和装饰Drawable，Drawable可以为null。
 * 垂直间隔和水平间隔分别设置
 * @Date 2020/6/22
 */
public abstract class GridVariedItemDecoration extends RecyclerView.ItemDecoration implements IVariedItemDecoration {

    /**
     * 是否显示最后一行的Divider，默认不显示
     */
    private int mLastDividerSize = 0;
    /**
     * 是否显示第一行的Divider，默认不显示
     */
    private int mFirstDividerSize = 0;

    private final Rect mBounds = new Rect();

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (!checkLayoutManager(parent)) {
            return;
        }

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int orientation = layoutManager.getOrientation();

        canvas.save();

        if (parent.getClipToPadding()) {
            // 设置绘制画布去掉padding
            canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                    parent.getWidth() - parent.getPaddingRight(), parent.getHeight() - parent.getPaddingBottom());
        }

        final int childCount = parent.getChildCount();

        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int itemCount = layoutManager.getItemCount();
        RecyclerView.Adapter adapter = parent.getAdapter();
        if(adapter instanceof BaseQuickAdapter) {
            itemCount = ((BaseQuickAdapter) adapter).getData().size();
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);

            int position = parent.getChildAdapterPosition(child);

            Drawable columnDivider = getColumnDivider(position);
            boolean isRightItem = isRightItem(position, spanCount, itemCount, spanSizeLookup, orientation);
            boolean hasRightDivider = columnDivider != null && !isRightItem;
            if (hasRightDivider) {
                final int right = mBounds.right + Math.round(child.getTranslationX());
                final int left = right - getColumnDividerSize(position);
                int top = mBounds.top;
                if(isShowFirstDivider() && isTopItem(position, spanCount, itemCount, spanSizeLookup, orientation)){
                    top += mFirstDividerSize;
                }
                columnDivider.setBounds(left, top, right, mBounds.bottom);
                columnDivider.draw(canvas);
            }

            Drawable rowDivider = getRowDivider(position);
            boolean isBottomItem = isBottomItem(position, spanCount, itemCount, spanSizeLookup, orientation);
            boolean hasBottomDivider = rowDivider != null && !isBottomItem;
            if (hasBottomDivider) {
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                final int top = bottom - getRowDividerSize(position);
                rowDivider.setBounds(mBounds.left, top, mBounds.right, bottom);
                rowDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        if (!checkLayoutManager(parent)) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int orientation = layoutManager.getOrientation();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int itemCount = layoutManager.getItemCount();
        int position = parent.getChildAdapterPosition(view);

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        boolean isRightItem = isRightItem(position, spanCount, itemCount, spanSizeLookup, orientation);
        boolean hasRightDivider = (orientation == RecyclerView.HORIZONTAL && isShowLastDivider()) || !isRightItem;
        if (hasRightDivider) {
            if(isRightItem){
                right = mLastDividerSize;
            }else{
                right = getColumnDividerSize(position);
            }
        }

        boolean isBottomItem = isBottomItem(position, spanCount, itemCount, spanSizeLookup, orientation);
        boolean hasBottomDivider = (orientation == RecyclerView.VERTICAL && isShowLastDivider()) || !isBottomItem;
        if (hasBottomDivider) {
            if(isBottomItem){
                bottom = mLastDividerSize;
            }else{
                bottom = getRowDividerSize(position);
            }
        }

        boolean hasTopDivider = (orientation == RecyclerView.VERTICAL && isShowFirstDivider());
        if (hasTopDivider) {
            if(isTopItem(position, spanCount, itemCount, spanSizeLookup, orientation)){
                top = mFirstDividerSize;
            }
        }

        outRect.set(left, top, right, bottom);
    }

    /**
     * 判断是否是位于右边的item
     *
     * @param view
     * @param position
     * @return
     */
    public boolean isRightItem(RecyclerView view, int position) {
        if (!checkLayoutManager(view)) {
            return false;
        }

        GridLayoutManager layoutManager = (GridLayoutManager) view.getLayoutManager();
        int orientation = layoutManager.getOrientation();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int itemCount = layoutManager.getItemCount();
        return isRightItem(position, spanCount, itemCount, spanSizeLookup, orientation);
    }

    /**
     * 判断是否是位于右边的item
     */
    private boolean isRightItem(int position, int spanCount, int itemCount, GridLayoutManager.SpanSizeLookup spanSizeLookup, int orientation) {

        if (orientation == RecyclerView.VERTICAL) {
            int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
            int positionSpanSize = spanSizeLookup.getSpanSize(position);
            return positionSpanSize + spanIndex == spanCount;
        } else {

            // 最后一列的开始位置
            int lastColumnPosition = itemCount - 1;
            while (lastColumnPosition >= 0 && spanSizeLookup.getSpanIndex(lastColumnPosition, spanCount) != 0) {
                lastColumnPosition--;
            }

            return lastColumnPosition <= position;
        }
    }

    /**
     * 判断是否是位于底部的item
     *
     * @param view
     * @param position
     * @return
     */
    public boolean isBottomItem(RecyclerView view, int position) {
        if (!checkLayoutManager(view)) {
            return false;
        }

        GridLayoutManager layoutManager = (GridLayoutManager) view.getLayoutManager();
        int orientation = layoutManager.getOrientation();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int itemCount = layoutManager.getItemCount();
        return isBottomItem(position, spanCount, itemCount, spanSizeLookup, orientation);
    }

    /**
     * 判断是否是位于底部的item
     */
    private boolean isBottomItem(int position, int spanCount, int itemCount, GridLayoutManager.SpanSizeLookup spanSizeLookup, int orientation) {

        if (orientation == RecyclerView.VERTICAL) {
            // 最后一行的开始位置
            int lastRowPosition = itemCount - 1;
            while (lastRowPosition >= 0 && spanSizeLookup.getSpanIndex(lastRowPosition, spanCount) != 0) {
                lastRowPosition--;
            }
            return lastRowPosition <= position;
        } else {
            int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
            int positionSpanSize = spanSizeLookup.getSpanSize(position);
            return positionSpanSize + spanIndex == spanCount;
        }
    }

    /**
     * 判断是否是位于顶部的item
     */
    private boolean isTopItem(int position, int spanCount, int itemCount, GridLayoutManager.SpanSizeLookup spanSizeLookup, int orientation) {
        if (orientation == RecyclerView.VERTICAL) {
            return position < spanCount;
        } else {
            return position % spanCount == 0;
        }
    }

    /**
     * 设置是否显示最后一行的Divider，默认不显示
     */
    public GridVariedItemDecoration setLastDividerSize(float size) {
        mLastDividerSize = (int) DividerUtils.dp2px(size);
        return this;
    }

    public GridVariedItemDecoration setFirstDividerSize(float size){
        mFirstDividerSize = (int) DividerUtils.dp2px(size);
        return this;
    }

    @Override
    public boolean isShowLastDivider() {
        return mLastDividerSize > 0;
    }

    @Override
    public boolean isShowFirstDivider() {
        return mFirstDividerSize > 0;
    }

    @Override
    public boolean checkLayoutManager(RecyclerView view) {
        return view.getLayoutManager() != null && view.getLayoutManager() instanceof GridLayoutManager;
    }
}
