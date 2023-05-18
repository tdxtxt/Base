package com.tdxtxt.liteavplayer.video

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
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.controller.GestureController
import com.tdxtxt.liteavplayer.video.controller.NetworkController
import com.tdxtxt.liteavplayer.video.controller.OrientationController
import com.tdxtxt.liteavplayer.video.controller.view.BasicControllerView
import com.tdxtxt.liteavplayer.video.controller.view.BrightControllerView
import com.tdxtxt.liteavplayer.video.controller.view.MultipleControllerView
import com.tdxtxt.liteavplayer.video.controller.view.VolumeControllerView
import com.tdxtxt.liteavplayer.video.inter.*
import com.tencent.rtmp.TXVodPlayer
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_video.view.*


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   : 腾讯点播
 * </pre>
 */
class TXVideoPlayerView : FrameLayout, IVideoView, IVideoPlayer, TXPlayerListener,
    LifecycleObserver {
    private var mVideoMgr: VideoMananger? = null
    private var mWidthRatio = -1
    private var mHeightRatio = -1
    private lateinit var mBaicView: BasicControllerView
    private lateinit var mGestureController: GestureController
    private lateinit var mOrientationController: OrientationController
    private lateinit var mNetworkController: NetworkController
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView
    private lateinit var mMultipleControllerView: MultipleControllerView
    private val mControllerList: MutableList<IController> = ArrayList()
    private var mPlayerEventListenerListRef: MutableList<TXPlayerListener>? = null
    private var mMultipleList: List<Float>? = null

    private var mFullChangelisenter: ((isFullScreen: Boolean) -> Unit)? = null
    private var mOrientationType = OrientationController.VERITCAL

    fun setVideoManager(manager: VideoMananger){
        mVideoMgr = manager
        mVideoMgr?.removeEventListener(this)
        mVideoMgr?.addPlayerEventListener(this)
        getBaicView().bindSurface()
    }
    fun getVideoManager() = mVideoMgr

    constructor(context: Context): super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        if(attrs != null){
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.TXVideoPlayerView)
            mHeightRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txHeightRatio, -1)
            mWidthRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txWidthRatio, -1)
            attributes.recycle()
        }
        initView(context)
    }

    private fun initView(context: Context) {
        mControllerList.clear()
        mBaicView = BasicControllerView(context)
        mBaicView.attach(this)
        mControllerList.add(mBaicView)

        mOrientationController = OrientationController()
        mOrientationController.attach(this)
        mControllerList.add(mOrientationController)

        mGestureController = GestureController()
        mGestureController.attach(this)
        mControllerList.add(mGestureController)

        mNetworkController = NetworkController()
        mNetworkController.attach(this)
        mControllerList.add(mNetworkController)

        mVolumeControllerView = VolumeControllerView(context)
        mVolumeControllerView.attach(this)
        mControllerList.add(mVolumeControllerView)

        mBrightControllerView = BrightControllerView(context)
        mBrightControllerView.attach(this)
        mControllerList.add(mBrightControllerView)

        mMultipleControllerView = MultipleControllerView(context)
        mMultipleControllerView.attach(this)
        mControllerList.add(mMultipleControllerView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(mWidthRatio > 0 && mHeightRatio > 0){
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width.toFloat() / mWidthRatio.toFloat() * mHeightRatio.toFloat())
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
     * 设置标题
     */
    fun setTitle(title: CharSequence?){
        getBaicView().setTitle(title)
    }

    /**
     * 设置动态水印
     */
    fun setWaterMark(dynamicWatermarkTip: String?, tipTextSize: Int, tipTextColor: Int){
        getBaicView().setWaterMark(dynamicWatermarkTip, tipTextSize, tipTextColor)
    }

    /**
     * 可拖动的最大时长百分段，取值0到1
     */
    fun setTrackMaxPercent(trackMaxPercent: Float){
        getBaicView().getSeekBarControllerView().setTrackMaxPercent(trackMaxPercent)
    }

    /**
     * 设置倍速可选项列表
     */
    fun setMultipleList(multipleList: List<Float>?){
        mMultipleList = multipleList
    }

    /**
     * 获取倍速可选项列表
     */
    fun getMultipleList() = mMultipleList

    override fun showCustomView(iView: AbsCustomController) {
        hideCustomView()
        val view = iView.inflater(this)
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

    override fun back() {
        if(onBackPressed()){
            release()
            getActivity()?.finish()
        }
    }

    override fun isFullScreen(): Boolean {
        return isReverseFullScreen() || isForwardFullScreen()
    }

    fun isReverseFullScreen() = mOrientationType == OrientationController.HORIZONTA_REVERSE
    fun isForwardFullScreen() = mOrientationType == OrientationController.HORIZONTA_FORWARD

    override fun stopFullScreen(){
        if(!isFullScreen()) return
        val activity = getActivity() ?: return

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

    override fun startFullScreen(isReverse: Boolean?){
        if(isReverseFullScreen() == isReverse) return
        val activity = getActivity() ?: return

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

    fun getActivity(): Activity? {
        val activity = context
        if(activity is Activity) return activity
        return null
    }

    fun setFullChangeLisenter(lisenter: (isFullScreen: Boolean) -> Unit){
        this.mFullChangelisenter = lisenter
    }

    override fun onPlayStateChanged(state: Int, value: Any?) {
        when(state){
            TXPlayerListener.PlayerState.EVENT_PREPARED -> {
                getBaicView().hideLoading()
            }
            TXPlayerListener.PlayerState.EVENT_START -> {
                keepScreenOn = true//禁止熄屏
                getBaicView().updatePlayButton(false)
            }
            TXPlayerListener.PlayerState.EVENT_PLAYING -> {
                getBaicView().updateTextTime(getCurrentDuration(), getDuration())
                getBaicView().updateSeekBar(getCurrentPercentage(), getBufferedPercentage())
            }
            TXPlayerListener.PlayerState.EVENT_PAUSED -> {
                keepScreenOn = false//允许熄屏
                getBaicView().updatePlayButton(true)
            }
            TXPlayerListener.PlayerState.EVENT_LOADING -> {
                if(value is Boolean){
                    if(value)  getBaicView().showLoading() else  getBaicView().hideLoading()
                }
            }
            TXPlayerListener.PlayerState.CHANGE_NETSPEED -> {
                if(value is Int){
                    getBaicView().updateNetspeed(value)
                }
            }
            TXPlayerListener.PlayerState.EVENT_COMPLETED -> {

            }
            TXPlayerListener.PlayerState.EVENT_RELEASE -> {
                destoryView()
            }
            TXPlayerListener.PlayerState.CHANGE_MULTIPLE -> {
                getBaicView().updateMultiple(getMultiple())
            }
        }

        mPlayerEventListenerListRef?.forEach { it.onPlayStateChanged(state, value) }
    }

    private fun destoryView(){
        mControllerList.forEach {
            it.detach()
        }
        mPlayerEventListenerListRef?.clear()
        mControllerList.clear()
        mFullChangelisenter = null
    }

    /************************************* 播放相关方法重写开始 *************************************/
    override fun getPlayer(): TXVodPlayer? {
        return mVideoMgr?.getPlayer()
    }

    override fun setToken(token: String?) {
        mVideoMgr?.setToken(token)
    }

    override fun addPlayerEventListener(listener: TXPlayerListener?) {
        if(listener == null) return
        if(mPlayerEventListenerListRef == null) mPlayerEventListenerListRef = ArrayList()
        mPlayerEventListenerListRef?.add(listener)
    }

    override fun removeEventListener(listener: TXPlayerListener?) {
        if(listener == null) return
        mPlayerEventListenerListRef?.remove(listener)
    }

    override fun setDataSource(appId: Int, fileId: String?, psign: String?, startTime: Int?, autoPlay: Boolean) {
        mVideoMgr?.setDataSource(appId, fileId, psign, startTime, autoPlay)
        getBaicView().showBasicMenuLayout()
        getBaicView().showLoading()
    }

    override fun setDataSource(path: String?, startTime: Int?, autoPlay: Boolean) {
        mVideoMgr?.setDataSource(path, startTime, autoPlay)
        getBaicView().showBasicMenuLayout()
        getBaicView().showLoading()
    }

    override fun reStart() {
        mVideoMgr?.reStart()
    }

    override fun resume() {
        mVideoMgr?.resume()
        waterMark?.show()
    }

    override fun pause() {
        mVideoMgr?.pause()
        waterMark?.hide()
    }

    override fun stop(clearFrame: Boolean) {
        mVideoMgr?.stop(clearFrame)
    }

    override fun togglePlay() {
        mVideoMgr?.togglePlay()
    }

    override fun isPlaying(): Boolean {
        return mVideoMgr?.isPlaying()?: false
    }

    override fun seekTo(time: Int) {
        mVideoMgr?.seekTo(time)
    }

    override fun release() {
        mVideoMgr?.release()
    }

    override fun isRelease(): Boolean {
        return mVideoMgr?.isRelease()?: false
    }

    override fun getCurrentDuration(): Int {
        return mVideoMgr?.getCurrentDuration()?: 0
    }

    override fun getDuration(): Int {
        return mVideoMgr?.getDuration()?: 0
    }

    override fun getCurrentPercentage(): Int {
        return mVideoMgr?.getCurrentPercentage()?: 0
    }

    override fun getBufferedPercentage(): Int {
        return mVideoMgr?.getBufferedPercentage()?: 0
    }

    override fun getBufferedDuration(): Int {
        return mVideoMgr?.getBufferedDuration()?: 0
    }

    override fun setLooping(isLooping: Boolean) {
        mVideoMgr?.setLooping(isLooping)
    }

    override fun setMultiple(speed: Float) {
        mVideoMgr?.setMultiple(speed)
    }

    override fun getMultiple(): Float {
        return mVideoMgr?.getMultiple()?: 1f
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
        mControllerList.forEach { it.onResume() }

        if(!isBackgroundPlaying){
            if(mPauseBeforePlaying == true) resume()
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        mControllerList.forEach { it.onPause() }

        if(!isBackgroundPlaying){
            mPauseBeforePlaying = isPlaying()
            pause()
        }
    }
}