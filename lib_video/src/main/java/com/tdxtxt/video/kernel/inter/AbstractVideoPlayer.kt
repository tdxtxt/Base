package com.tdxtxt.video.kernel.inter

import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tdxtxt.video.utils.PlayerConstant
import java.lang.ref.WeakReference
import java.util.*


/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 抽象的播放器
 * </pre>
 */
abstract class AbstractVideoPlayer : IVideoPlayer {
    /**
     * 初始化播放器实例
     * 视频播放器第一步：创建视频播放器
     */
    abstract fun initPlayer()

    /**
     * 绑定渲染视频的View,主要用于TextureView
     * @param surface surface
     */
    abstract fun bindSurface(surfaceView: SurfaceView)
    /**
     * 解绑渲染视频的View
     * @param surface surface
     */
    abstract fun unbindSurface()

    abstract fun getVideoWidth(): Int

    abstract fun getVideoHeight(): Int

    /*----------------------------第三部分：player绑定view后，需要监听播放状态--------------------*/
    /**
     * 播放器事件回调
     */
    private var mPlayerEventListenerListRef: WeakHashMap<VideoPlayerListener, Int>? = null

    private var mPlayerEventListenerRef: WeakReference<VideoPlayerListener?>? = null

    fun setPlayerEventListener(listener: VideoPlayerListener?){
        if(listener == null){
            mPlayerEventListenerRef?.clear()
            mPlayerEventListenerRef = null
            return
        }
        mPlayerEventListenerRef = WeakReference(listener)
    }
    /**
     * 绑定VideoView，监听播放异常，完成，开始准备，视频size变化，视频信息等操作
     */
    fun addPlayerEventListener(listener: VideoPlayerListener) {
        if(mPlayerEventListenerListRef == null) mPlayerEventListenerListRef = WeakHashMap()
        mPlayerEventListenerListRef?.put(listener, 0)
    }

    fun removePlayerEventListener(listener: VideoPlayerListener?){
        mPlayerEventListenerListRef?.remove(listener)
    }

    fun removePlayerEventListener(){
        mPlayerEventListenerListRef?.clear()
    }

    private fun sendPlayerEvent(@PlayerConstant.PlaylerState state: Int, value: Any? = null){
        mPlayerEventListenerRef?.let {
            it.get()?.apply {
                onPlayStateChanged(state, value)
            }
        }

        mPlayerEventListenerListRef?.forEach {
            it.key?.onPlayStateChanged(state, value)
        }
    }

    protected fun sendErrorEvent(error: String?){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_ERROR, error)
    }

    protected fun sendCompleteEvent(){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_COMPLETED)
    }

    protected fun sendIdleEvent(){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_IDLE)
    }

    protected fun sendStartEvent(){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_START)
    }

    protected fun sendPlayingEvent(second: Int){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_PLAYING, second)
    }

    protected fun sendPauseEvent(){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_PAUSED)
    }

    protected fun sendPreparedEvent(){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_PREPARED)
    }

    protected fun sendBufferEvent(progress: Float){
        sendPlayerEvent(PlayerConstant.PlaylerState.STATE_BUFFERING, progress)
    }

    protected fun sendVideoSizeChangedEvent(width: Int, height: Int){
        sendPlayerEvent(PlayerConstant.PlaylerState.CHANGE_VIDEO_SIZE)
    }

    protected fun shendMultipleChangeEvent(value: Float){
        sendPlayerEvent(PlayerConstant.PlaylerState.CHANGE_MULTIPLE, value)
    }

}