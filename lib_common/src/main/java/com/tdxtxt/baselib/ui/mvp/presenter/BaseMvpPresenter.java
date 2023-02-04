package com.tdxtxt.baselib.ui.mvp.presenter;

import android.os.Bundle;

import com.tdxtxt.baselib.ui.mvp.BaseMvpView;


/**
 * 功能描述:
 * @author tangdexiang
 * @since 2020/7/28
 */
public interface BaseMvpPresenter<V extends BaseMvpView> {
    void attach(V view);
    void restoreInstanceState(Bundle savedInstanceState);
    void saveInstanceState(Bundle outState);
    void resume();
    void detach();
    void detachOnPause();
}
