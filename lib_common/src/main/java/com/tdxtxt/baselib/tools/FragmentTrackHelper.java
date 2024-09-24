package com.tdxtxt.baselib.tools;

import android.os.Bundle;
import android.view.View;

import com.tdxtxt.baselib.callback.FragmentLifecycleCallbacks;

import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-12-11
 *     desc   : Fragment 生命周期监听
 * </pre>
 */
public class FragmentTrackHelper {
    // Fragment 的回调监听
    private static final Set<FragmentLifecycleCallbacks> FRAGMENT_CALLBACKS = new HashSet<>();
    /**
     * BaseFragment插入生命周期回调 created
     * @param object - Fragment
     * @param arguments - Bundle
     */
    public static void trackFragmentCreated(Object object, Bundle arguments){
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onCreate(object, arguments);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BaseFragment插入生命周期回调 onViewCreated
     * @param object - Fragment
     * @param rootView - View
     * @param bundle - Bundle
     */
    public static void trackFragmentViewCreated(Object object, View rootView, Bundle bundle){
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onViewCreated(object, rootView, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * BaseFragment插入生命周期回调 onResume
     * @param object Fragment
     */
    public static void trackFragmentResume(Object object) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onResume(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BaseFragment插入生命周期回调 onPause
     * @param object Fragment
     */
    public static void trackFragmentPause(Object object) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onPause(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * BaseFragment插入生命周期回调 setUserVisibleHint
     *
     * @param object Fragment
     * @param isVisibleToUser 是否可见
     */
    public static void trackFragmentSetUserVisibleHint(Object object, boolean isVisibleToUser) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.setUserVisibleHint(object, isVisibleToUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BaseFragment插入生命周期回调 onHiddenChanged
     * @param object Fragment
     * @param hidden Fragment 是否隐藏
     */
    public static void trackFragmentOnHiddenChanged(Object object, boolean hidden) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onHiddenChanged(object, hidden);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BaseFragment插入生命周期回调 onDestroyView
     * @param object Fragment
     */
    public static void trackFragmentDestroyView(Object object) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onDestroyView(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BaseFragment插入生命周期回调 onDestroy
     * @param object Fragment
     */
    public static void trackFragmentDestroy(Object object) {
        if (!FragmentTrackHelper.isFragment(object)) {
            return;
        }
        for (FragmentLifecycleCallbacks fragmentCallbacks : FRAGMENT_CALLBACKS) {
            try {
                fragmentCallbacks.onDestroy(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加 Fragment 的回调监听
     * @param fragmentLifecycleCallbacks FragmentLifecycleCallbacks
     */
    public static void addFragmentCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        if (fragmentLifecycleCallbacks != null) {
            FRAGMENT_CALLBACKS.add(fragmentLifecycleCallbacks);
        }
    }

    /**
     * 移除指定的 Fragment 的回调监听
     * @param fragmentLifecycleCallbacks FragmentLifecycleCallbacks
     */
    public static void removeFragmentCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        if (fragmentLifecycleCallbacks != null) {
            FRAGMENT_CALLBACKS.remove(fragmentLifecycleCallbacks);
        }
    }

    public static void removeAllFragmentCallbacks() {
        FRAGMENT_CALLBACKS.clear();
    }

    /**
     * 判断 Object 是否是 Fragment
     *
     * @param object Object
     * @return true 是，false 不是
     */
    private static boolean isFragment(Object object) {
        try {
            if (object == null) {
                return false;
            }
            Class<?> supportFragmentClass = null;
            Class<?> androidXFragmentClass = null;
            Class<?> fragment = null;
            try {
                fragment = Class.forName("android.app.Fragment");
            } catch (Exception e) {
                //ignored
            }
            try {
                supportFragmentClass = Class.forName("android.support.v4.app.Fragment");
            } catch (Exception e) {
                //ignored
            }

            try {
                androidXFragmentClass = Class.forName("androidx.fragment.app.Fragment");
            } catch (Exception e) {
                //ignored
            }

            if (supportFragmentClass == null && androidXFragmentClass == null && fragment == null) {
                return false;
            }

            if ((supportFragmentClass != null && supportFragmentClass.isInstance(object)) ||
                    (androidXFragmentClass != null && androidXFragmentClass.isInstance(object)) ||
                    (fragment != null && fragment.isInstance(object))) {
                return true;
            }
        } catch (Exception e) {
            //ignored
        }
        return false;
    }
}
