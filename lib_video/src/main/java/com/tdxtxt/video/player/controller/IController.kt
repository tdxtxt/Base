package com.tdxtxt.video.player.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
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
    fun updateSeekBar(progress: Int?, secondaryProgress: Int? = null)
    fun updateTogglePlay(isPlaying: Boolean)
    fun updateFullScreen(isFullScreen: Boolean?)
    fun updateCover(resId: Int)
    fun updateBufferProgress(rate: Float)
    fun updateTime(current: Long?, total: Long?)
    fun changeVideoSize(widthSize: Int?, heightSize: Int?)
    fun updateMultiple(value: Float)
}

interface IControllerMultiple : IController {
    fun show()
    fun hide()
}

interface IControllerGesture : IController {
    fun show(changePercent: Float)
    fun hide()
}

interface IControllerSeekBar: IController

abstract class AbsControllerCustom: IController{
    private var mView: View? = null
    abstract fun getLayoutResId(): Int

    override fun detach() {
        mView = null
    }

    fun init(container: VideoPlayerView): View? {
        mView = LayoutInflater.from(container.context).inflate(getLayoutResId(), null, false)
        //禁止手势传递
        mView?.setOnTouchListener { v, event ->  true}
        attach(container)
        return mView
    }

    fun <T : View> findViewById(@IdRes id: Int): T? {
        return mView?.findViewById(id)
    }

}