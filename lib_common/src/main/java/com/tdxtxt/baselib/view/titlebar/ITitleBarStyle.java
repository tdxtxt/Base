package com.tdxtxt.baselib.view.titlebar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/TitleBar
 *    time   : 2018/08/20
 *    desc   : 默认参数接口
 */
public interface ITitleBarStyle {
    /**
     * 创建左标题 View
     */
    TextView createLeftView(Context context);

    /**
     * 创建中间标题 View
     */
    TextView createTitleView(Context context);

    /**
     * 获取右标题 View
     */
    TextView createRightView(Context context);

    /**
     * 获取分割线 View
     */
    View createLineView(Context context);

    /**
     * 创建标题栏背景
     */
    Drawable createBackgroundDrawable(Context context);

    /**
     * 创建子控件的水平内间距
     */
    int createHorizontalPadding(Context context);

    /**
     * 获取子控件的垂直内间距
     */
    int createVerticalPadding(Context context);
}