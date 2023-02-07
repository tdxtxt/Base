package com.tdxtxt.baselib.adapter.recycle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tdxtxt.baselib.adapter.recycle.bean.BaseListBody;

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

    public void updateData(boolean isFirstPage, BaseListBody<T> data){
        updateData(isFirstPage, data, true);
    }

    public void updateData(boolean isFirstPage, BaseListBody<T> data, boolean showLoadMoreView){
        if(data == null || data.list == null) return;
        if(isFirstPage){//下拉刷新
            setNewInstance(data.list);
        }else{//加载更多
            addData(data.list);
        }
        getLoadMoreModule().loadMoreComplete();
        if(!data.isNextPage()) getLoadMoreModule().loadMoreEnd(!showLoadMoreView);
    }
}
