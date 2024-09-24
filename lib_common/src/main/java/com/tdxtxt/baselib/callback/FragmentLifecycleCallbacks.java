package com.tdxtxt.baselib.callback;

import android.os.Bundle;
import android.view.View;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-12-11
 *     desc   :
 * </pre>
 */
public interface FragmentLifecycleCallbacks {
    void onCreate(Object object, Bundle arguments);
    void onViewCreated(Object object, View rootView, Bundle bundle);
    void onStart(Object object);
    void onResume(Object object);
    void onPause(Object object);
    void onStop(Object object);
    void onHiddenChanged(Object object, boolean hidden);
    void setUserVisibleHint(Object object, boolean isVisibleToUser);
    void onDestroyView(Object object);
    void onDestroy(Object object);
}
