package com.tdxtxt.baselib.view.recycler.divider;

import android.content.res.Resources;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/26
 *     desc   :
 * </pre>
 */
class DividerUtils {
    static float dp2px(float value){
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (value * scale + 0.5f);
    }

}
