package com.tdxtxt.video.player.view

import android.content.Context
import android.view.View
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer


/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/23
 *     desc   : 接口
 * </pre>
 */
interface IController {
    fun attachPlayer(videoPlayer: AbstractVideoPlayer)
}

interface IBaiseController : IController{
    fun getContext(): Context
    fun getView(): View
    fun getViewWidth(): Int
    fun toggleMenu()
    fun showMenu()
    fun hideMenu()
    fun scrollSeekBar(time: Long)
    fun updateStartUI()
    fun updatePauseUI()
    fun updateCover(resId: Int)
    fun updateBufferProgress(rate: Float)
    fun updateTime(current: Long, total: Long)
    fun changeVideoSize(widthSize: Int, heightSize: Int)
    fun changeProgress(current: Long)
}