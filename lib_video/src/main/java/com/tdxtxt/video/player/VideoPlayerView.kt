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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.tdxtxt.video.R
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.kernel.inter.VideoPlayerListener
import com.tdxtxt.video.player.controller.GestureController
import com.tdxtxt.video.player.controller.AbsControllerCustom
import com.tdxtxt.video.player.controller.OrientationController
import com.tdxtxt.video.player.view.BrightControllerView
import com.tdxtxt.video.player.view.MultipleControllerView
import com.tdxtxt.video.player.view.VolumeControllerView
import com.tdxtxt.video.player.view.WrapperControlView
import com.tdxtxt.video.utils.PlayerConstant
import com.tdxtxt.video.utils.PlayerUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 视频控件
 * </pre>
 */
class VideoPlayerView : FrameLayout, IVideoPlayer,
    IVideoView, VideoPlayerListener, androidx.lifecycle.LifecycleObserver {
    private var mWrapperView: WrapperControlView = WrapperControlView(context).apply { attach(this@VideoPlayerView) }
    private var mVideoPlayer: AbstractVideoPlayer? = null
    private var videoWidthRatio = -1
    private var videoHeightRatio = -1
    private var videoRadius = 0f
    private var isBackgroundPlaying = false
    private var mPauseBeforePlaying: Boolean? = null //熄屏之前是否播放
    private lateinit var mOrientationController: OrientationController
    private lateinit var mGestureController: GestureController
    private lateinit var mMultipleControllerView: MultipleControllerView
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView

    private var mOrientationType = PlayerConstant.VERITCAL

    private var mFullChangelisenter: ((isFullScreen: Boolean) -> Unit)? = null

    constructor(context: Context) : super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.VideoPlayerView)
        videoWidthRatio = attributes.getInteger(R.styleable.VideoPlayerView_videoWidthRatio, -1)
        videoHeightRatio = attributes.getInteger(R.styleable.VideoPlayerView_videoHeightRatio, -1)
        videoRadius = attributes.getDimension(R.styleable.VideoPlayerView_videoRadius, 0f)
        attributes.recycle()
        initView(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(videoWidthRatio > 0 && videoHeightRatio > 0){
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width.toFloat() / videoWidthRatio.toFloat() * videoHeightRatio.toFloat())
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY))
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(isPlaying() && isBackgroundPlaying){
            //退出后不进行播放器的销毁
            destoryView()
        }else{
            release()
        }
    }

    private fun initView(context: Context){
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

    fun onBackPressed(): Boolean{
        if (isFullScreen()) {
            stopFullScreen()
            return false
        }
        else {
            return true
        }
    }

    override fun setVideoPlayer(player: AbstractVideoPlayer) {
        this.mVideoPlayer = player
        mVideoPlayer?.setPlayerEventListener(this)
        mWrapperView.bindSurface()
        mWrapperView.updateMultiple(mVideoPlayer?.getSpeed()?: 1f)
    }

    override fun showCustomView(iView: AbsControllerCustom) {
        hideCustomView()
        val view = iView.init(this)
        view?.id = R.id.video_customview
        if(view != null) mWrapperView.addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun hideCustomView() {
        val customView: View? = mWrapperView.findViewById(R.id.video_customview)
        if(customView != null) mWrapperView.removeView(customView)
    }

    override fun setCover(resId: Int) {
        mWrapperView.updateCover(resId)
    }

    override fun supportBackgroundPlaying(isBackgroundPlaying: Boolean) {
        this.isBackgroundPlaying = isBackgroundPlaying
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
                mWrapperView.updateSeekBar(getVideoPlayer()?.getCurrentPercentage(), getVideoPlayer()?.getBufferedPercentage())
                mWrapperView.updateTime(getVideoPlayer()?.getCurrentDuration(), getVideoPlayer()?.getDuration())
            }
            PlayerConstant.PlaylerState.STATE_START -> {
                keepScreenOn = true//禁止熄屏
                mWrapperView.updateTogglePlay(false)
            }
            PlayerConstant.PlaylerState.STATE_PAUSED -> {
                keepScreenOn = false//允许熄屏
                mWrapperView.updateTogglePlay(true)
            }
            PlayerConstant.PlaylerState.STATE_BUFFERING -> {
                mWrapperView.updateBufferProgress(if (value is Float) value else 1f)
            }
            PlayerConstant.PlaylerState.STATE_PLAYING -> {
                mWrapperView.updateSeekBar(getVideoPlayer()?.getCurrentPercentage(), getVideoPlayer()?.getBufferedPercentage())
                mWrapperView.updateTime(getVideoPlayer()?.getCurrentDuration(), getVideoPlayer()?.getDuration())
            }
            PlayerConstant.PlaylerState.STATE_COMPLETED -> {
                keepScreenOn = false//允许熄屏
                mWrapperView.updateTogglePlay(true)
            }
            PlayerConstant.PlaylerState.CHANGE_VIDEO_SIZE -> {
                mWrapperView.changeVideoSize(getVideoPlayer()?.getVideoWidth(), getVideoPlayer()?.getVideoHeight())
            }
            PlayerConstant.PlaylerState.CHANGE_MULTIPLE -> {
                mWrapperView.updateMultiple(if(value is Float) value else 1f)
            }
            PlayerConstant.PlaylerState.STATE_RELEASE ->{
                destoryView()
            }
        }
    }

    override fun release() {
        mVideoPlayer?.release()
    }

    fun destoryView(){
        mFullChangelisenter = null

        mOrientationController.detach()
        mGestureController.detach()
        mMultipleControllerView.detach()
        mVolumeControllerView.detach()
        mBrightControllerView.detach()
        mWrapperView.detach()
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

    override fun getCurrentPercentage(): Int {
        return mVideoPlayer?.getCurrentPercentage()?: 0
    }

    override fun getBufferedPercentage(): Int {
        return mVideoPlayer?.getBufferedPercentage()?: 0
    }

    override fun getBufferedDuration(): Long {
        return mVideoPlayer?.getBufferedDuration()?: 0L
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        if(!isBackgroundPlaying){
            if(mPauseBeforePlaying == true) start()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        if(!isBackgroundPlaying){
            mPauseBeforePlaying = isPlaying()
            pause()
        }
    }

    /**
     * 可拖动的最大时长百分段，取值0到1
     */
    fun setTrackMaxPercent(trackMaxPercent: Float){
        mWrapperView.setTrackMaxPercent(trackMaxPercent)
    }

    /**
     * 绑定生命周期，用以控制熄屏后视频是否任然播放
     */
    fun bindLifecycle(owner: LifecycleOwner?) {
        owner?.lifecycle?.apply {
            removeObserver(this@VideoPlayerView)
            addObserver(this@VideoPlayerView)
        }
    }
}