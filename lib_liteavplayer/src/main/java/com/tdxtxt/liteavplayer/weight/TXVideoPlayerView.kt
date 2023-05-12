package com.tdxtxt.liteavplayer.weight

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.tdxtxt.liteavplayer.LiteAVManager
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.inter.IVideoPlayer
import com.tdxtxt.liteavplayer.inter.TXPlayerListener
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.weight.controller.GestureController
import com.tdxtxt.liteavplayer.weight.controller.OrientationController
import com.tdxtxt.liteavplayer.weight.controller.view.BaiscControllerView
import com.tdxtxt.liteavplayer.weight.controller.view.BrightControllerView
import com.tdxtxt.liteavplayer.weight.controller.view.MultipleControllerView
import com.tdxtxt.liteavplayer.weight.controller.view.VolumeControllerView
import com.tdxtxt.liteavplayer.weight.inter.AbsControllerCustom
import com.tdxtxt.liteavplayer.weight.inter.IVideoView
import com.tencent.rtmp.TXVodPlayer


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   : 腾讯点播
 * </pre>
 */
class TXVideoPlayerView : FrameLayout, IVideoView, IVideoPlayer, TXPlayerListener,
    LifecycleObserver {
    private var mLiteMgr: LiteAVManager? = null
    private var videoWidthRatio = -1
    private var videoHeightRatio = -1
    private lateinit var mBaicView: BaiscControllerView
    private lateinit var mGestureController: GestureController
    private lateinit var mOrientationController: OrientationController
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView
    private lateinit var mMultipleControllerView: MultipleControllerView
    private var mFullChangelisenter: ((isFullScreen: Boolean) -> Unit)? = null
    private var mOrientationType = OrientationController.VERITCAL

    fun setPlayerManager(manager: LiteAVManager){
        mLiteMgr = manager
        mLiteMgr?.removeEventListener(this)
        mLiteMgr?.addPlayerEventListener(this)
        getBaicView().bindSurface()
    }

    constructor(context: Context): super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        if(attrs != null){
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.TXVideoPlayerView)
            videoHeightRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txVideoHeightRatio, -1)
            videoWidthRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txVideoWidthRatio, -1)
            attributes.recycle()
        }
        initView(context)
    }

    private fun initView(context: Context) {
        mBaicView = BaiscControllerView(context)
        mBaicView.attach(this)

        mOrientationController = OrientationController()
        mOrientationController.attach(this)

        mGestureController = GestureController()
        mGestureController.attach(this)

        mVolumeControllerView = VolumeControllerView(context)
        mVolumeControllerView.attach(this)

        mBrightControllerView = BrightControllerView(context)
        mBrightControllerView.attach(this)

        mMultipleControllerView = MultipleControllerView(context)
        mMultipleControllerView.attach(this)
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

    fun getBaicView() = mBaicView
    fun getBrightControllerView() = mBrightControllerView
    fun getVolumeControllerView() = mVolumeControllerView
    fun getMultipleControllerView() = mMultipleControllerView

    /**
     * 可拖动的最大时长百分段，取值0到1
     */
    fun setTrackMaxPercent(trackMaxPercent: Float){
        getBaicView().getSeekBarControllerView().setTrackMaxPercent(trackMaxPercent)
    }

    override fun showCustomView(iView: AbsControllerCustom) {
        hideCustomView()
        val view = iView.init(this)
        view?.id = R.id.txvideo_customview
        if(view != null) getBaicView().addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun hideCustomView() {
        val customView: View? = getBaicView().findViewById(R.id.txvideo_customview)
        if(customView != null) getBaicView().removeView(customView)
    }

    override fun onBackPressed(): Boolean {
        if (isFullScreen()) {
            stopFullScreen()
            return false
        }else {
            return true
        }
    }

    override fun isFullScreen(): Boolean {
        return isReverseFullScreen() || isForwardFullScreen()
    }

    fun isReverseFullScreen() = mOrientationType == OrientationController.HORIZONTA_REVERSE
    fun isForwardFullScreen() = mOrientationType == OrientationController.HORIZONTA_FORWARD

    override fun stopFullScreen(){
        if(!isFullScreen()) return
        val activity = context
        if(activity is Activity){
            if(activity.isFinishing || activity.isDestroyed) return

            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            mOrientationType = OrientationController.VERITCAL

            LiteavPlayerUtils.showSysBar(activity)
            mFullChangelisenter?.invoke(isFullScreen())

            getBaicView().updateFullScreen(isFullScreen())
            val parentView = getBaicView().parent
            if(parentView is ViewGroup){
                parentView.removeView(getBaicView())
            }
            addView(getBaicView(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    override fun startFullScreen(isReverse: Boolean?){
        if(isReverseFullScreen() == isReverse) return

        val activity = context
        if(activity is Activity){
            if(activity.isFinishing || activity.isDestroyed) return
            if(isReverse == true || mOrientationController.isReverseHorizontal()){
                mOrientationType = OrientationController.HORIZONTA_REVERSE
                if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
            }else{
                mOrientationType = OrientationController.HORIZONTA_FORWARD
                if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }

            LiteavPlayerUtils.hideSysBar(activity)
            mFullChangelisenter?.invoke(isFullScreen())

            getBaicView().updateFullScreen(isFullScreen())
            val parentView = getBaicView().parent
            if(parentView is ViewGroup){
                parentView.removeView(getBaicView())
            }
            val dectorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            dectorView.addView(getBaicView(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    fun setFullChangeLisenter(lisenter: (isFullScreen: Boolean) -> Unit){
        this.mFullChangelisenter = lisenter
    }

    override fun onPlayStateChanged(state: Int, value: Any?) {
        when(state){
            TXPlayerListener.PlayerState.STATE_PREPARED -> {
                getBaicView().hideLoading()
            }
            TXPlayerListener.PlayerState.STATE_START -> {
                keepScreenOn = true//禁止熄屏
                getBaicView().updatePlayButton(false)
            }
            TXPlayerListener.PlayerState.STATE_PLAYING -> {
                getBaicView().updateTextTime(getCurrentDuration(), getDuration())
                getBaicView().updateSeekBar(getCurrentPercentage(), getBufferedPercentage())
            }
            TXPlayerListener.PlayerState.STATE_PAUSED -> {
                keepScreenOn = false//允许熄屏
                getBaicView().updatePlayButton(true)
            }
            TXPlayerListener.PlayerState.STATE_LOADING -> {
                if(value is Boolean){
                    if(value)  getBaicView().showLoading() else  getBaicView().hideLoading()
                }
            }
            TXPlayerListener.PlayerState.STATE_NETSPEED -> {
                if(value is Int){
                    getBaicView().updateNetspeed(value)
                }
            }
            TXPlayerListener.PlayerState.STATE_COMPLETED -> {

            }
            TXPlayerListener.PlayerState.STATE_RELEASE -> {
                destoryView()
            }
        }
    }

    private fun destoryView(){
        getBaicView().detach()
        mOrientationController.detach()
        mGestureController.detach()
        mVolumeControllerView.detach()
        mBrightControllerView.detach()
        mFullChangelisenter = null
    }

    /************************************* 播放相关方法重写开始 *************************************/
    override fun getPlayer(): TXVodPlayer? {
        return mLiteMgr?.getPlayer()
    }

    override fun setDataSource(appId: Int, fileId: String?, psign: String?, startTime: Int?, autoPlay: Boolean) {
        mLiteMgr?.setDataSource(appId, fileId, psign, startTime, autoPlay)
    }

    override fun setDataSource(path: String?, startTime: Int?, autoPlay: Boolean) {
        mLiteMgr?.setDataSource(path, startTime, autoPlay)
    }

    override fun reStart() {
        mLiteMgr?.reStart()
    }

    override fun resume() {
        mLiteMgr?.resume()
    }

    override fun pause() {
        mLiteMgr?.pause()
    }

    override fun togglePlay() {
        mLiteMgr?.togglePlay()
    }

    override fun isPlaying(): Boolean {
        return mLiteMgr?.isPlaying()?: false
    }

    override fun seekTo(time: Int) {
        mLiteMgr?.seekTo(time)
    }

    override fun release() {
        mLiteMgr?.release()
    }

    override fun isRelease(): Boolean {
        return mLiteMgr?.isRelease()?: false
    }

    override fun getCurrentDuration(): Int {
        return mLiteMgr?.getCurrentDuration()?: 0
    }

    override fun getDuration(): Int {
        return mLiteMgr?.getDuration()?: 0
    }

    override fun getCurrentPercentage(): Int {
        return mLiteMgr?.getCurrentPercentage()?: 0
    }

    override fun getBufferedPercentage(): Int {
        return mLiteMgr?.getBufferedPercentage()?: 0
    }

    override fun getBufferedDuration(): Int {
        return mLiteMgr?.getBufferedDuration()?: 0
    }

    override fun setLooping(isLooping: Boolean) {
        mLiteMgr?.setLooping(isLooping)
    }

    override fun setMultiple(speed: Float) {
        mLiteMgr?.setMultiple(speed)
    }

    override fun getMultiple(): Float {
        return mLiteMgr?.getMultiple()?: 1f
    }

    /************************************* 生命周期方法 *************************************/
    private var isBackgroundPlaying = false //是否允许后台播放
    private var mPauseBeforePlaying: Boolean? = null //熄屏之前是否播放
    fun setBackgroundPlaying(isPlaying: Boolean){
        isBackgroundPlaying = isPlaying
    }
    /**
     * 绑定生命周期，用以控制熄屏后视频是否任然播放
     */
    fun bindLifecycle(owner: LifecycleOwner?) {
        owner?.lifecycle?.apply {
            removeObserver(this@TXVideoPlayerView)
            addObserver(this@TXVideoPlayerView)
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        if(!isBackgroundPlaying){
            if(mPauseBeforePlaying == true) resume()
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        if(!isBackgroundPlaying){
            mPauseBeforePlaying = isPlaying()
            pause()
        }
    }
}