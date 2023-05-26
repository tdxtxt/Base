package com.tdxtxt.baselib.adapter.recycle;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tdxtxt.baselib.adapter.recycle.bean.AbsListBody;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/25
 *     desc   : 多布局
 * </pre>
 */
public abstract class BaseProviderMultiItemLoadMoreAdapter<T extends MultiItemEntity> extends BaseProviderMultiAdapter<T> {

    public BaseProviderMultiItemLoadMoreAdapter(){
        this(null);
    }

    public BaseProviderMultiItemLoadMoreAdapter(List<T> data){
        super(data);
        addItemTypeLayout();
    }

    protected abstract void addItemTypeLayout();

    @Override
    protected int getItemType(@NotNull List<? extends T> list, int position) {
        return list.get(position).getItemType();
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
