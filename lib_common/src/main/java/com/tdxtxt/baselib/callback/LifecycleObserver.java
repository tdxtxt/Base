package com.tdxtxt.baselib.callback;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/7
 *     desc   :
 * </pre>
 */
public class LifecycleObserver implements androidx.lifecycle.LifecycleObserver {
    WeakReference<LifecycleMethod> method;
    public LifecycleObserver(LifecycleMethod method){
        this.method = new WeakReference<>(method);
    }
    LifecycleMethod getMethod(){
        if(method == null) return null;
        return method.get();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        LifecycleMethod method = getMethod();
        if(method != null) method.onCreate();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        LifecycleMethod method = getMethod();
        if(method != null) method.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        LifecycleMethod method = getMethod();
        if(method != null) method.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LifecycleMethod method = getMethod();
        if(method != null) method.onDestroy();
    }
}
