package com.tdxtxt.video.player

import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.player.controller.AbsControllerCustom

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 自定义控制器接口
 * </pre>
 */
interface IVideoView : IVideoPlayer {
    /**
     * 设置播放内核
     */
    fun setVideoPlayer(player: AbstractVideoPlayer)

    /**
     * 显示自定义view
     */
    fun showCustomView(iView: AbsControllerCustom)

    /**
     * 隐藏自定义view
     */
    fun hideCustomView()

    /**
     * 设置封面
     */
    fun setCover(resId: Int)

    /**
     * 是否后台播放
     */
    fun supportBackgroundPlaying(isBackgroundPlaying: Boolean)

}