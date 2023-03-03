package com.tdxtxt.video.player.controller

import android.content.Context
import com.tdxtxt.video.player.VideoPlayerView


/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/23
 *     desc   : 接口
 * </pre>
 */
interface IController {
    fun attach(container: VideoPlayerView)
    fun detach()
}

interface IControllerWrapper : IController {
    fun getContext(): Context
    fun getViewWidth(): Int
    fun getViewHeight(): Int
    fun toggleMenu()
    fun setTrackingSeekBar(isTrackingSeekBar: Boolean)
    fun showMenu()
    fun hideMenu()
    fun bindSurface()
    fun unBindSurface()
    fun updateSeekBar(progress: Float?)
    fun updateTogglePlay(isPlaying: Boolean)
    fun updateFullScreen(isFullScreen: Boolean?)
    fun updateCover(resId: Int)
    fun updateBufferProgress(rate: Float)
    fun updateTime(current: Long?, total: Long? = null)
    fun changeVideoSize(widthSize: Int?, heightSize: Int?)
    fun updateMultiple(value: Float)
}

interface IControllerMultiple : IController {
    fun show()
    fun hide()
}

interface IControllerVolume : IController {
    fun show(changePercent: Float)
    fun hide()
}