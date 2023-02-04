package com.tdxtxt.baselib.ui.mvp.presenter;

import android.os.Bundle;

import com.tdxtxt.baselib.ui.mvp.BaseMvpView;


/**
 * 功能描述:
 * @author tangdexiang
 * @since 2020/7/28
 */
public abstract class AbsPresenter<V extends BaseMvpView> implements BaseMvpPresenter<V> {
    protected V baseView;

    @Override
    public void attach(V view) {
        baseView = view;
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void saveInstanceState(Bundle outState) {

    }

    @Override
    public void detach() {

    }

    @Override
    public void detachOnPause() {

    }

    @Override
    public void resume() {

    }
}
