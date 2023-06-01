package com.tdxtxt.baselib.adapter.recycle;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tdxtxt.baselib.adapter.recycle.bean.AbsListBody;

public abstract class BaseMultiItemQuickLoadMoreAdapter<T extends MultiItemEntity, VH extends BaseViewHolder> extends BaseMultiItemQuickAdapter<T, VH> implements LoadMoreModule {
    public BaseMultiItemQuickLoadMoreAdapter(){
        super();
//        setEmptyView(R.layout.base_statelayout_empty);
        init();
    }

    /**
     * 该方法主要解决：如果在构造方法中调用抽象方法，可能会导致子类还未实现该方法而出现错误
     */
    private void init(){
        addItemTypeLayout();
    }

    protected abstract void addItemTypeLayout();

    @Override
    protected int getDefItemViewType(int position) {
        return super.getDefItemViewType(position);
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

    public void refreshItem(T item){
        if(item == null) return;
        int index = getData().indexOf(item);
        if (index < 0) {
            return;
        }
        setData(index, item);
    }
}
