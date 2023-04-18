package com.tdxtxt.tablayout.tools

import android.content.res.Resources

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/17
 *     desc   :
 * </pre>
 */
object TabUtils {
    @JvmStatic
    fun dp2px(dpValue: Int) = dp2px(dpValue.toFloat())
    @JvmStatic
    fun dp2px(dpValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f)
    }
    @JvmStatic
    fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }
    @JvmStatic
    fun px2dp(pxValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f)
    }
}