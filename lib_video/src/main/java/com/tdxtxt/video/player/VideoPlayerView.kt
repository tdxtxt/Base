package com.tdxtxt.video.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import com.tdxtxt.video.VideoPlayerManager
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.kernel.inter.VideoPlayerListener
import com.tdxtxt.video.player.controller.GestureController
import com.tdxtxt.video.player.controller.OrientationController
import com.tdxtxt.video.player.view.BrightControllerView
import com.tdxtxt.video.player.view.ControlWrapperView
import com.tdxtxt.video.player.view.MultipleControllerView
import com.tdxtxt.video.player.view.VolumeControllerView
import com.tdxtxt.video.utils.PlayerConstant
import com.tdxtxt.video.utils.PlayerUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
class VideoPlayerView constructor(
    context: Context,
    val mManager: VideoPlayerManager,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr), IVideoPlayer by mManager.player,
    IVideoView, VideoPlayerListener {
    private var mWrapperView: ControlWrapperView
    private var mOrientationController: OrientationController
    private var mGestureController: GestureController
    private var mMultipleControllerView: MultipleControllerView
    private var mVolumeControllerView: VolumeControllerView
    private var mBrightControllerView: BrightControllerView

    private var mOrientationType = PlayerConstant.VERITCAL

    constructor(context: Context) : this(context, VideoPlayerManager.newInstance(), null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, VideoPlayerManager.newInstance(), attributeSet, 0)

    init{
        mManager.player.setPlayerEventListener(this)
        mWrapperView = ControlWrapperView(context)
            .apply { attach(this@VideoPlayerView) }
        mGestureController = GestureController()
            .apply { attach(this@VideoPlayerView) }
        mOrientationController = OrientationController()
            .apply { attach(this@VideoPlayerView) }
        mMultipleControllerView = MultipleControllerView(context)
            .apply { attach(this@VideoPlayerView) }
        mVolumeControllerView = VolumeControllerView(context)
            .apply { attach(this@VideoPlayerView) }
        mBrightControllerView = BrightControllerView(context)
            .apply { attach(this@VideoPlayerView) }

    }

    fun getVideoPlayerManager() = mManager
    fun getVideoPlayer() = mManager.getVideoPlayer()
    fun getControlWrapper() = mWrapperView
    fun getMultipleContronller() = mMultipleControllerView
    fun getVolumeController() = mVolumeControllerView
    fun getBrightController() = mBrightControllerView

    override fun showCustomView(view: View) {
        TODO("Not yet implemented")
    }

    override fun hideCustomView() {
        TODO("Not yet implemented")
    }

    override fun setCover(resId: Int) {
        mWrapperView.updateCover(resId)
    }

    override fun setRound(round: Float) {
        TODO("Not yet implemented")
    }

    override fun onPlayStateChanged(@PlayerConstant.PlaylerState state: Int, value: Any?) {
        when(state){
            PlayerConstant.PlaylerState.STATE_PREPARED -> {
                mWrapperView.updateTime(getVideoPlayer().getCurrentDuration(), getVideoPlayer().getDuration())
            }
            PlayerConstant.PlaylerState.STATE_START -> {
                keepScreenOn = true
                mWrapperView.updateTogglePlay(false)
            }
            PlayerConstant.PlaylerState.STATE_PAUSED -> {
                keepScreenOn = false
                mWrapperView.updateTogglePlay(true)
            }
            PlayerConstant.PlaylerState.STATE_BUFFERING -> {
                mWrapperView.updateBufferProgress(if (value is Float) value else 1f)
            }
            PlayerConstant.PlaylerState.STATE_PLAYING -> {
                mWrapperView.changeProgress(getVideoPlayer().getCurrentDuration())
            }
            PlayerConstant.PlaylerState.CHANGE_VIDEO_SIZE -> {
                mWrapperView.changeVideoSize(getVideoPlayer().getVideoWidth(), getVideoPlayer().getVideoHeight())
            }
            PlayerConstant.PlaylerState.CHANGE_MULTIPLE -> {
                mWrapperView.changeMultiple(if(value is Float) value else 1f)
            }
        }
    }

    fun isFullScreen() = isReverseFullScreen() || isForwardFullScreen()
    fun isReverseFullScreen() = mOrientationType == PlayerConstant.HORIZONTA_REVERSE
    fun isForwardFullScreen() = mOrientationType == PlayerConstant.HORIZONTA_FORWARD

    fun stopFullScreen(){
        if(!isFullScreen()) return
        val activity = context
        if(activity is Activity){
            if(activity.isFinishing || activity.isDestroyed) return

            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            mOrientationType = PlayerConstant.VERITCAL
            mWrapperView.updateFullScreen(isFullScreen())
            val parentView = mWrapperView.parent
            if(parentView is ViewGroup){
                parentView.removeView(mWrapperView)
            }
            addView(mWrapperView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    fun startFullScreen(isReverse: Boolean? = null){
        if(isReverseFullScreen() == isReverse) return

        val activity = context
        if(activity is Activity){
            if(activity.isFinishing || activity.isDestroyed) return
            if(isReverse == true || mOrientationController.isReverseHorizontal()){
                mOrientationType = PlayerConstant.HORIZONTA_REVERSE
                if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
            }else{
                mOrientationType = PlayerConstant.HORIZONTA_FORWARD
                if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }

            PlayerUtils.hideSysBar(activity)

            mWrapperView.updateFullScreen(isFullScreen())
            val parentView = mWrapperView.parent
            if(parentView is ViewGroup){
                parentView.removeView(mWrapperView)
            }
            val dectorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            dectorView.addView(mWrapperView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        }
    }

    fun onBackPressed(){
        if(isFullScreen()){
            stopFullScreen()
        }else{
            release()
            val activity = context
            if(activity is Activity){
                activity.finish()
            }
        }
    }

    override fun release() {
        mManager.release()
        mOrientationController.detach()
        mGestureController.detach()
        mMultipleControllerView.detach()
    }
}