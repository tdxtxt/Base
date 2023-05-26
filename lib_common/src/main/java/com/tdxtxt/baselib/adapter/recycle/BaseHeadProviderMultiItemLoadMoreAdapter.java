package com.tdxtxt.baselib.adapter.recycle;

import androidx.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/24
 *     desc   : 携带header头部的多布局
 * </pre>
 */
public abstract class BaseHeadProviderMultiItemLoadMoreAdapter<T extends SectionEntity> extends BaseProviderMultiItemLoadMoreAdapter<T> {
    private @LayoutRes int sectionHeadResId;
    public BaseHeadProviderMultiItemLoadMoreAdapter(@LayoutRes int sectionHeadResId){
        this(sectionHeadResId, null);
    }
    public BaseHeadProviderMultiItemLoadMoreAdapter(@LayoutRes int sectionHeadResId, List<T> data){
        super(data);
        this.sectionHeadResId = sectionHeadResId;
        addItemProvider(new HeadViewProvider<T>(sectionHeadResId));
        addItemTypeLayout();
    }

    protected abstract void convertHeader(@NotNull BaseViewHolder holder, T o);

    private static class HeadViewProvider<T extends SectionEntity> extends BaseItemProvider<T>{
        private @LayoutRes int sectionHeadResId;
        private HeadViewProvider(@LayoutRes int sectionHeadResId){
            this.sectionHeadResId = sectionHeadResId;
        }
        @Override
        public int getItemViewType() {
            return SectionEntity.HEADER_TYPE;
        }

        @Override
        public int getLayoutId() {
            return sectionHeadResId;
        }

        @Override
        public void convert(@NotNull BaseViewHolder holder, T o) {
            BaseProviderMultiAdapter<T> adapter = getAdapter();
            if(adapter instanceof BaseHeadProviderMultiItemLoadMoreAdapter){
                ((BaseHeadProviderMultiItemLoadMoreAdapter<T>) adapter).convertHeader(holder, o);
            }
        }
    }
}
