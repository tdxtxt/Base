package com.tdxtxt.baselib.view.recycler.divider;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/26
 *     desc   :
 * </pre>
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int verticalDividerSize;//行间距，垂直间距
    private int horizontalDividerSize;//列间距，水平间距
    private int mFirstDividerSize = 0;


    public GridItemDecoration(float spacing) {
        this(spacing, spacing);
    }

    public GridItemDecoration(float verticalDividerSize, float horizontalDividerSize){
        this.verticalDividerSize = (int) dp2px(verticalDividerSize);
        this.horizontalDividerSize = (int) dp2px(horizontalDividerSize);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(!checkLayoutManager(parent)) return;
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if(layoutManager == null) return;
        int spanCount = layoutManager.getSpanCount();
//        int orientation = layoutManager.getOrientation();
//        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
//        int itemCount = layoutManager.getItemCount();

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        outRect.left = column * horizontalDividerSize / spanCount; // column * ((1f / spanCount) * spacing)
        outRect.right = horizontalDividerSize - (column + 1) * horizontalDividerSize / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
        if (position >= spanCount) {
            outRect.top = verticalDividerSize; // item top
        }else{
            if(isShowFirstDivider()){
                outRect.top = mFirstDividerSize;
            }
        }
    }

    private boolean isShowFirstDivider() {
        return mFirstDividerSize > 0;
    }

    public GridItemDecoration setFirstDividerSize(float size) {
        mFirstDividerSize = (int) dp2px(size);
        return this;
    }

    private boolean checkLayoutManager(RecyclerView view) {
        return view.getLayoutManager() != null && view.getLayoutManager() instanceof GridLayoutManager;
    }

    private float dp2px(float value){
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (value * scale + 0.5f);
    }
}
