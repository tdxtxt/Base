package com.tdxtxt.baselib.ui.mvp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tdxtxt.baselib.ui.CommToolBarActivity;
import com.tdxtxt.baselib.ui.mvp.presenter.BaseMvpPresenter;
import com.tdxtxt.baselib.ui.mvp.presenter.PresenterDelegate;


/**
 * @作者： ton
 * @时间： 2018\4\28 0028
 * @描述： 创建Presenter方式：重写createPresenter方法；
 * @传入参数说明：
 * @返回参数说明：
 */
public abstract class CommToolBarMvpActivity extends CommToolBarActivity {
    private PresenterDelegate<BaseMvpView> mvpDelegate = new PresenterDelegate();

    protected abstract BaseMvpPresenter<?> createPresenter();
    protected abstract BaseMvpView createMvpView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mvpDelegate.delegate((BaseMvpPresenter<BaseMvpView>) createPresenter());
        mvpDelegate.attach(createMvpView());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvpDelegate.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mvpDelegate.detachOnPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mvpDelegate.saveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvpDelegate.detach();
    }
}
