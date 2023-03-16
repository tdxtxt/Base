package com.tdxtxt.video.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.AssetFileDescriptor
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
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
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr), IVideoPlayer,
    IVideoView, VideoPlayerListener {
    private var mWrapperView: ControlWrapperView
    private var mVideoPlayer: AbstractVideoPlayer? = null
    private lateinit var mOrientationController: OrientationController
    private lateinit var mGestureController: GestureController
    private lateinit var mMultipleControllerView: MultipleControllerView
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView

    private var mOrientationType = PlayerConstant.VERITCAL

    private var mFullChangelisenter: ((isFullScreen: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    init{
        mWrapperView = ControlWrapperView(context)
            .apply { attach(this@VideoPlayerView) }
        if(!isInEditMode){
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
    }

    fun getVideoPlayer() = mVideoPlayer
    fun getControlWrapper() = mWrapperView
    fun getMultipleContronller() = mMultipleControllerView
    fun getVolumeController() = mVolumeControllerView
    fun getBrightController() = mBrightControllerView

    fun isFullScreen() = isReverseFullScreen() || isForwardFullScreen()
    fun isReverseFullScreen() = mOrientationType == PlayerConstant.HORIZONTA_REVERSE
    fun isForwardFullScreen() = mOrientationType == PlayerConstant.HORIZONTA_FORWARD

    fun setFullChangeLisenter(lisenter: (isFullScreen: Boolean) -> Unit){
        this.mFullChangelisenter = lisenter
    }

    fun stopFullScreen(){
        if(!isFullScreen()) return
        val activity = context
        if(activity is Activity){
            if(activity.isFinishing || activity.isDestroyed) return

            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            mOrientationType = PlayerConstant.VERITCAL

            PlayerUtils.showSysBar(activity)
            mFullChangelisenter?.invoke(isFullScreen())

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
            mFullChangelisenter?.invoke(isFullScreen())

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

    override fun setVideoPlayer(player: AbstractVideoPlayer) {
        this.mVideoPlayer = player
        mVideoPlayer?.setPlayerEventListener(this)
        mWrapperView.bindSurface()
        mWrapperView.updateMultiple(mVideoPlayer?.getSpeed()?: 1f)
    }

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

    override fun setDataSource(path: String?) {
        mVideoPlayer?.setDataSource(path)
    }

    override fun setDataSource(fd: AssetFileDescriptor?) {
        mVideoPlayer?.setDataSource(fd)
    }

    override fun prepare() {
        mVideoPlayer?.prepare()
    }

    override fun start() {
        mVideoPlayer?.start()
    }

    override fun reStart() {
        mVideoPlayer?.reStart()
    }

    override fun pause() {
        mVideoPlayer?.pause()
    }

    override fun togglePlay() {
        mVideoPlayer?.togglePlay()
    }

    override fun stop() {
        mVideoPlayer?.stop()
    }

    override fun isPlaying(): Boolean {
        return mVideoPlayer?.isPlaying()?: false
    }

    override fun seekTo(time: Long) {
        mVideoPlayer?.seekTo(time)
    }

    override fun accurateSeekTo(time: Long) {
        mVideoPlayer?.accurateSeekTo(time)
    }

    override fun onPlayStateChanged(@PlayerConstant.PlaylerState state: Int, value: Any?) {
        when(state){
            PlayerConstant.PlaylerState.STATE_PREPARED -> {
                mWrapperView.updateSeekBar(getVideoPlayer()?.getCurrentPercent())
                mWrapperView.updateTime(getVideoPlayer()?.getCurrentDuration(), getVideoPlayer()?.getDuration())
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
                mWrapperView.updateSeekBar(getVideoPlayer()?.getCurrentPercent())
                mWrapperView.updateTime(getVideoPlayer()?.getCurrentDuration(), getVideoPlayer()?.getDuration())
            }
            PlayerConstant.PlaylerState.CHANGE_VIDEO_SIZE -> {
                mWrapperView.changeVideoSize(getVideoPlayer()?.getVideoWidth(), getVideoPlayer()?.getVideoHeight())
            }
            PlayerConstant.PlaylerState.CHANGE_MULTIPLE -> {
                mWrapperView.updateMultiple(if(value is Float) value else 1f)
            }
            PlayerConstant.PlaylerState.STATE_RELEASE ->{
                destory()
            }
        }
    }

    override fun release() {
        mVideoPlayer?.release()
    }

    fun destory(){
        mFullChangelisenter = null

        mOrientationController.detach()
        mGestureController.detach()
        mMultipleControllerView.detach()
    }

    override fun isRelease(): Boolean {
        return mVideoPlayer?.isRelease()?: true
    }

    override fun getCurrentDuration(): Long {
        return mVideoPlayer?.getCurrentDuration()?: 0
    }

    override fun getDuration(): Long {
        return mVideoPlayer?.getDuration()?: 0
    }

    override fun getBufferedPercentage(): Int {
        return mVideoPlayer?.getBufferedPercentage()?: 0
    }

    override fun setVolume(volume: Float) {
        mVideoPlayer?.setVolume(volume)
    }

    override fun getVolume(): Float {
        return mVideoPlayer?.getVolume()?: 0f
    }

    override fun setLooping(isLooping: Boolean) {
        mVideoPlayer?.setLooping(isLooping)
    }

    override fun setSpeed(speed: Float) {
        mVideoPlayer?.setSpeed(speed)
    }

    override fun getSpeed(): Float {
        return mVideoPlayer?.getSpeed()?: 1f
    }

    override fun getTcpSpeed(): Long {
        return mVideoPlayer?.getTcpSpeed()?: 0
    }
}