package com.tdxtxt.baselib.view.recycler.divider;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/26
 *     desc   :
 * </pre>
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mDividerSize;

    private int mLastDividerSize = 0;
    private int mFirstDividerSize = 0;
    private int mMarginHorizontal = 0;
    private final Rect mBounds = new Rect();

    public LinearItemDecoration(float spacing, @ColorInt int color) {
        this(spacing, new ColorDrawable(color));
    }

    public LinearItemDecoration(float spacing, Drawable divider) {
        mDividerSize = (int) dp2px(spacing);
        mDivider = divider;
    }

    public LinearItemDecoration(float spacing) {
        this(spacing, null);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (!checkLayoutManager(parent)) {
            return;
        }

        canvas.save();

        if (parent.getClipToPadding()) {
            // 设置绘制画布去掉padding
            canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                    parent.getWidth() - parent.getPaddingRight(), parent.getHeight() - parent.getPaddingBottom());
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if(layoutManager == null) return;
        int orientation = layoutManager.getOrientation();
        int itemCount = layoutManager.getItemCount();
        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        if(adapter instanceof BaseQuickAdapter){
            itemCount = ((BaseQuickAdapter<?, ?>)adapter).getData().size();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (isLastItem(position, itemCount)) {
                continue;
            }

            parent.getDecoratedBoundsWithMargins(child, mBounds);
            if (orientation == RecyclerView.VERTICAL) {
                Drawable divider = getRowDivider(position);
                if (divider != null) {
                    final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                    int top;
                    if(isShowFirstDivider() && isFirstItem(position, itemCount)){
                        top = bottom - mFirstDividerSize;
                    }else if(isShowLastDivider() && isLastItem(position, itemCount)){
                        top = bottom - mLastDividerSize;
                    }else{
                        top = bottom - getRowDividerSize(position);
                    }

                    divider.setBounds(mBounds.left + mMarginHorizontal, top, mBounds.right - mMarginHorizontal, bottom);
                    divider.draw(canvas);
                }
            } else {
                Drawable divider = getRowDivider(position);
                if (divider != null) {
                    final int right = mBounds.right + Math.round(child.getTranslationX());
                    int left;
                    if(isShowFirstDivider() && isFirstItem(position, itemCount)){
                        left = right - mFirstDividerSize;
                    }else if(isShowLastDivider() && isLastItem(position, itemCount)){
                        left = right - mLastDividerSize;
                    }else{
                        left = right - getRowDividerSize(position);
                    }

                    divider.setBounds(left, mBounds.top + mMarginHorizontal, right, mBounds.bottom - mMarginHorizontal);
                    divider.draw(canvas);
                }
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (!checkLayoutManager(parent)) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if(layoutManager == null) return;
        int orientation = layoutManager.getOrientation();
        int itemCount = layoutManager.getItemCount();
        int position = parent.getChildAdapterPosition(view);

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        if(isShowFirstDivider() && isFirstItem(position, itemCount)){
            if (orientation == RecyclerView.VERTICAL) {
                top = mFirstDividerSize;
                bottom = getRowDividerSize(position);
            }else{
                left = mFirstDividerSize;
                right = getRowDividerSize(position);
            }
        }

        if(isShowLastDivider() && isLastItem(position, itemCount)){
            if (orientation == RecyclerView.VERTICAL) {
                bottom = mLastDividerSize;
            }else{
                right = mLastDividerSize;
            }
        }

        if(!isFirstItem(position, itemCount) && !isLastItem(position, itemCount)){
            if (orientation == RecyclerView.VERTICAL) {
                bottom = getRowDividerSize(position);
            }else{
                right = getRowDividerSize(position);
            }
        }

        if(!isShowFirstDivider() && isFirstItem(position, itemCount)){
            if (orientation == RecyclerView.VERTICAL) {
                bottom = getRowDividerSize(position);
            }else{
                right = getRowDividerSize(position);
            }
        }
        outRect.set(left, top, right, bottom);
    }

    /**
     * 判断是否是最后一个item，最后一个item不添加Divider
     */
    private boolean isLastItem(int position, int itemCount) {
        return position >= itemCount - 1;
    }

    /**
     * 判断是否是第一个item，第一个item不添加Divider
     */
    private boolean isFirstItem(int position, int itemCount) {
        return position == 0;
    }

    /**
     * 设置是否显示最后一个项的Divider，默认不显示
     */
    public LinearItemDecoration setLastDividerSize(float size) {
        mLastDividerSize = (int) dp2px(size);
        return this;
    }

    /**
     * 设置是否显示最后一个项的Divider，默认不显示
     */
    public LinearItemDecoration setFirstDividerSize(float size) {
        mFirstDividerSize = (int) dp2px(size);
        return this;
    }

    public LinearItemDecoration setMarginHorizontal(float margin){
        mMarginHorizontal = (int) dp2px(margin);
        return this;
    }

    private boolean isShowLastDivider() {
        return mLastDividerSize > 0;
    }

    private boolean isShowFirstDivider() {
        return mFirstDividerSize > 0;
    }

    private int getRowDividerSize(int position) {
        return mDividerSize;
    }

    private Drawable getRowDivider(int position) {
        return mDivider;
    }

    private boolean checkLayoutManager(RecyclerView view) {
        return view.getLayoutManager() != null && view.getLayoutManager() instanceof LinearLayoutManager;
    }

    private float dp2px(float value){
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (value * scale + 0.5f);
    }
}
