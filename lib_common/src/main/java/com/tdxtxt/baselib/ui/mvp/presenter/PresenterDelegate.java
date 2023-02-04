package com.tdxtxt.baselib.ui.mvp.presenter;

import android.os.Bundle;

import com.tdxtxt.baselib.ui.mvp.BaseMvpView;


public class PresenterDelegate<V extends BaseMvpView> extends AbsPresenter<V> {
    public BaseMvpPresenter<V> presenter = new AbsPresenter<V>(){};

    public void delegate(BaseMvpPresenter<V> presenter){
        this.presenter = presenter;
    }

    @Override
    public void attach(V view) {
        super.attach(view);
        if(presenter != null) presenter.attach(view);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        super.restoreInstanceState(savedInstanceState);
        if(presenter != null) presenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);
        if(presenter != null) presenter.saveInstanceState(outState);
    }

    @Override
    public void detach() {
        super.detach();
        if(presenter != null) presenter.detach();
    }

    @Override
    public void detachOnPause() {
        super.detachOnPause();
        if(presenter != null) presenter.detachOnPause();
    }

    @Override
    public void resume() {
        super.resume();
        if(presenter != null) presenter.resume();
    }
}
