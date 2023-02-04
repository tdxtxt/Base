package com.tdxtxt.baselib.ui.mvp;


import android.app.Activity;

import com.tdxtxt.baselib.ui.IView;

/**
 * 功能描述:
 * @author gjq
 * @since 2021/7/28
 */
public interface BaseMvpView extends IView {
    public <T extends Activity> T getMVPActivity();
}
