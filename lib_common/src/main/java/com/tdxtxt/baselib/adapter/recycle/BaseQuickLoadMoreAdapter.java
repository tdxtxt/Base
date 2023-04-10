package com.tdxtxt.baselib.adapter.recycle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tdxtxt.baselib.adapter.recycle.bean.AbsListBody;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public abstract class BaseQuickLoadMoreAdapter<T, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> implements LoadMoreModule {
    public BaseQuickLoadMoreAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
//        setEmptyView(R.layout.base_statelayout_empty);
    }

    public BaseQuickLoadMoreAdapter(int layoutResId) {
        super(layoutResId);
//        setEmptyView(R.layout.base_statelayout_empty);
    }

    public <R extends AbsListBody<T>> void updateData(boolean isFirstPage, R data){
        updateData(isFirstPage, data, true);
    }

    public <R extends AbsListBody<T>> void updateData(boolean isFirstPage, R data, boolean showLoadMoreView){
        if(data == null || data.getDataList() == null) return;
        if(isFirstPage){//下拉刷新
            setNewInstance(data.getDataList());
        }else{//加载更多
            addData(data.getDataList());
        }
        getLoadMoreModule().loadMoreComplete();
        if(!data.hasNextPage()) getLoadMoreModule().loadMoreEnd(!showLoadMoreView);
    }
}
