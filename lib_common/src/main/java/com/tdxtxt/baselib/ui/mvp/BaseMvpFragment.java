package com.tdxtxt.baselib.ui.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tdxtxt.baselib.ui.BaseFragment;
import com.tdxtxt.baselib.ui.mvp.presenter.BaseMvpPresenter;
import com.tdxtxt.baselib.ui.mvp.presenter.PresenterDelegate;


/**
 * MVP模式：Fragment 基类
 */
public abstract class BaseMvpFragment extends BaseFragment {
    private PresenterDelegate<BaseMvpView> mvpDelegate = new PresenterDelegate();

    public abstract BaseMvpPresenter<?> createPresenter();
    public abstract BaseMvpView createMvpView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mvpDelegate.delegate((BaseMvpPresenter<BaseMvpView>) createPresenter());
        mvpDelegate.attach(createMvpView());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mvpDelegate.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mvpDelegate.detachOnPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mvpDelegate.saveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mvpDelegate.detach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public <T extends Activity> T getMVPActivity(){
        return getParentActivity();
    }

}
