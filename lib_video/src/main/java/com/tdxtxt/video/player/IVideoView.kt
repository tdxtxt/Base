package com.tdxtxt.video.player

import android.view.View
import com.tdxtxt.video.kernel.inter.IVideoPlayer

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 自定义控制器接口
 * </pre>
 */
interface IVideoView : IVideoPlayer {

    /**
     * 显示自定义view
     */
    fun showCustomView(view: View)

    /**
     * 隐藏自定义view
     */
    fun hideCustomView()

    /**
     * 设置封面
     */
    fun setCover(resId: Int)

    /**
     * 设置圆角
     */
    fun setRound(round: Float)

    /**
     * 横竖屏切换
     */
//    fun onConfigurationChanged(newConfig: Configuration)
}