package com.tdxtxt.baselib.view.recycler.divider;

import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author donkingliang QQ:1043214265 github:https://github.com/donkingliang
 * @Description 定义VariedItemDecoration的接口模板
 * @Date 2020/6/22
 */
public interface IVariedItemDecoration {

    /**
     * 是否显示最后一行的行Decoration
     */
    boolean isShowLastDivider();

    /**
     * 是否显示第一行的行Decoration
     */
    boolean isShowFirstDivider();

    /**
     * 根据下标返回行Decoration的size
     *
     * @param position
     * @return
     */
    int getRowDividerSize(int position);

    /**
     * 根据下标返回行Decoration的Drawable
     *
     * @param position
     * @return
     */
    Drawable getRowDivider(int position);

    /**
     * 根据下标返回列Decoration的size
     *
     * @param position
     * @return
     */
    int getColumnDividerSize(int position);

    /**
     * 根据下标返回列Decoration的Drawable
     *
     * @param position
     * @return
     */
    Drawable getColumnDivider(int position);

    /**
     * 检测是否是可支持的LayoutManager
     *
     * @param view
     * @return
     */
    boolean checkLayoutManager(RecyclerView view);

}
