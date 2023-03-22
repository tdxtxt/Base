package com.tdxtxt.baselib.tools

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.blankj.utilcode.util.BarUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/15
 *     desc   :
 * </pre>
 */
object StatusBarHelper {
    fun setPaddingStatusBarHeight(view: View?){
        if(view is ViewGroup){
            view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop() + BarUtils.getStatusBarHeight(), view.getPaddingRight(),
                view.getPaddingBottom()
            )
        }
    }

    fun setMarginStatusBarHeight(view: View?){
        val lp = view?.layoutParams
        if (lp is MarginLayoutParams) {
            val mlp = lp
            mlp.topMargin = BarUtils.getStatusBarHeight() + mlp.topMargin
            view.layoutParams = mlp
        }
    }

    /**
     * 设置状态栏图标白色主题【白色背景；黑色文字】
     */
    fun setLightMode(activity: Activity?){
        if(activity == null) return
        setStatusBar(activity, Color.WHITE)
    }
    /**
     * 设置状态栏图片黑色主题【黑色背景；白色文字】
     */
    fun setDarkMode(activity: Activity?){
        if(activity == null) return
        setStatusBar(activity, Color.BLACK)
    }

    /**
     * 设置状态栏透明【内容全入侵】
     * @param isLight: true-白色文字；false-黑色文字
     */
    fun setStatusBarFullTransparent(activity: Activity?, isLight: Boolean) {
        if (activity == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                decorView.apply {
                    if(isLight){
                        systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    }else{
                        systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                    }
                }
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT

                activity.findViewById<FrameLayout?>(android.R.id.content)
                    ?.getChildAt(0)?.fitsSystemWindows = false
            }
        }
    }

    fun setStatusBar(activity: Activity?, @ColorInt color: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            activity?.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = color
                decorView.apply {
                    if(isLightColor(color)){
                        systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }else{
                        systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }
                }
            }
        }
    }

    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }

    fun hideSysBar(activity: Activity?) {
        activity?.window?.apply {
            var uiOptions = decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }

            decorView.systemUiVisibility = uiOptions
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}