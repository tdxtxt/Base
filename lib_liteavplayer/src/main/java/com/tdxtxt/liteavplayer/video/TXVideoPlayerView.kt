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
import com.tdxtxt.liteavplayer.video.bean.BitrateItem
import com.tdxtxt.liteavplayer.video.controller.GestureController
import com.tdxtxt.liteavplayer.video.controller.NetworkController
import com.tdxtxt.liteavplayer.video.controller.OrientationController
import com.tdxtxt.liteavplayer.video.controller.PlayErrorController
import com.tdxtxt.liteavplayer.video.controller.view.*
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
class TXVideoPlayerView : FrameLayout, IVideoView, IVideoPlayer, TXPlayerListener, LifecycleObserver {
    private var mVideoMgr: VideoMananger? = null
    private var mWidthRatio = -1
    private var mHeightRatio = -1
    private var mPlayerStyle = 0 //0表示点播样式，具有进度条等样式；1表示直播样式，不具有进度条等的样式
    private lateinit var mBaicView: BasicControllerView
    private lateinit var mGestureController: GestureController
    private lateinit var mOrientationController: OrientationController
    private lateinit var mNetworkController: NetworkController
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView
    private lateinit var mMultipleControllerView: MultipleControllerView
    private lateinit var mBitrateControllerView: BitrateControllerView
    private val mControllerList: MutableList<IController> = ArrayList()
    private var mPlayerEventListenerListRef: MutableList<TXPlayerListener>? = null
    private var mMultipleList: List<Float>? = null

    private var mScreenChangelisenter: MutableList<(ScreenChangeLisenter)>? = null
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
            mWidthRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_tx_width_ratio, -1)
            mHeightRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_tx_height_ratio, -1)
            mPlayerStyle = attributes.getInteger(R.styleable.TXVideoPlayerView_tx_player_style, 0)
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
        if(!isInEditMode) mOrientationController.attach(this)
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

        mMultipleControllerView = MultipleControllerView()
        mMultipleControllerView.attach(this)
        mControllerList.add(mMultipleControllerView)

        mBitrateControllerView = BitrateControllerView()
        mBitrateControllerView.attach(this)
        mControllerList.add(mBitrateControllerView)
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
    fun getBitrateControllerView() = mBitrateControllerView

    override fun setVodStyle() {
        mPlayerStyle = 0
        getBaicView().configStyle()
    }

    override fun setLiveStyle() {
        mPlayerStyle = 1
        getBaicView().configStyle()
    }

    fun getPlayerStyle() = mPlayerStyle

    /**
     * 设置标题
     */
    override fun setTitle(title: CharSequence?){
        getTitleView()?.text = title
    }

    fun getTitleView() = getBaicView().getTitleTextView()

    /**
     * 设置动态水印
     */
    override fun setWaterMark(dynamicWatermarkTip: String?, tipTextSize: Int, tipTextColor: Int){
        getBaicView().setWaterMark(dynamicWatermarkTip, tipTextSize, tipTextColor)
    }

    /**
     * 可拖动的最大时长百分段，取值0到1 Control
     */
    override fun setDragMaxPercent(dragMaxPercent: Float?){
        getBaicView().getSeekBarControllerView().setDragMaxPercent(dragMaxPercent)
    }

    /**
     * 设置倍速可选项列表
     */
    override fun setMultipleList(multipleList: List<Float>?){
        if(multipleList?.contains(1f) != true){
            val tempMultipleList = mutableListOf<Float>()
            tempMultipleList.add(1f)
            if(multipleList != null) tempMultipleList.addAll(multipleList)
            tempMultipleList.sortDescending()
            mMultipleList = tempMultipleList
        }else{
            val tempMultipleList = mutableListOf<Float>()
            tempMultipleList.addAll(multipleList)
            tempMultipleList.sortDescending()
            mMultipleList = tempMultipleList
        }
    }

    /**
     * 获取倍速可选项列表
     */
    override fun getMultipleList() = mMultipleList?: mutableListOf(1f)

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
        getTitleView()?.visibility = View.INVISIBLE
        mScreenChangelisenter?.forEach {
            it.onScreenChange(isFullScreen())
        }

        getBaicView().updateFullScreen(isFullScreen())
        getBaicView().moveBasicTopMenuLayout()
        val parentView = getBaicView().parent
        if(parentView is ViewGroup){
            parentView.removeView(getBaicView())
        }
        addView(getBaicView(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun startFullScreen(isReverse: Boolean?){
        if(isReverse == true && isReverseFullScreen()) return
        if(isReverse == false && isForwardFullScreen()) return
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
        getTitleView()?.visibility = View.VISIBLE
        mScreenChangelisenter?.forEach {
            it.onScreenChange(isFullScreen())
        }

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

    fun addScreenChangeLisenter(lisenter: ScreenChangeLisenter?){
        if(lisenter == null) return
        mScreenChangelisenter?.remove(lisenter)
        if(mScreenChangelisenter == null) mScreenChangelisenter = ArrayList()
        mScreenChangelisenter?.add(lisenter)
    }

    fun removeScreenChangeLisenter(lisenter: ScreenChangeLisenter?){
        if(lisenter == null) return
        mScreenChangelisenter?.remove(lisenter)
    }

    override fun onPlayStateChanged(state: Int, value: Any?) {
        when(state){
            TXPlayerListener.PlayerState.EVENT_PREPARED -> {
                getBaicView().hideLoading()
                getBaicView().updateBitrate(getCurrentBitrate())
            }
            TXPlayerListener.PlayerState.EVENT_START -> {
                keepScreenOn = true//禁止熄屏
                getBaicView().updatePlayButton(false)
                getBaicView().resumeBasicTopMenuLayout()
            }
            TXPlayerListener.PlayerState.EVENT_PLAYING -> {
                getBaicView().updateTextTime(getCurrentDuration(), getDuration())
                getBaicView().updateSeekBar(getCurrentPercentage(), getBufferedPercentage())
            }
            TXPlayerListener.PlayerState.EVENT_PAUSED -> {
                keepScreenOn = false//允许熄屏
                getBaicView().updatePlayButton(true)
                getBaicView().moveBasicTopMenuLayout()
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
                getBaicView().updatePlayButton(true)
                getBaicView().moveBasicTopMenuLayout()
            }
            TXPlayerListener.PlayerState.EVENT_RELEASE -> {
                destoryView()
            }
            TXPlayerListener.PlayerState.CHANGE_MULTIPLE -> {
                getBaicView().updateMultiple(getMultiple())
            }
            TXPlayerListener.PlayerState.EVENT_ERROR -> {
                showCustomView(PlayErrorController(value?.toString()))
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
        mScreenChangelisenter?.clear()
        mScreenChangelisenter = null
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

    override fun setFileIdSource(appId: Int, fileId: String?, psign: String?, startTime: Int?, autoPlay: Boolean) {
        mVideoMgr?.setFileIdSource(appId, fileId, psign, startTime, autoPlay)
        getBaicView().showBasicMenuLayout()
        getBaicView().showLoading()
    }

    override fun getDataSource(): String? {
        return mVideoMgr?.getDataSource()
    }

    /**
     * autoPlay如果为自动播放，需要注意可能存在自动播放失败的情况，因此加上延迟代码 post { resume() }
     */
    override fun setDataSource(path: String?, startTime: Int?, autoPlay: Boolean, enableHardWareDecode: Boolean?) {
        mVideoMgr?.setDataSource(path, startTime, autoPlay, enableHardWareDecode)
        if(autoPlay) post { resume() } //解决下一次设置资源后无法自动播放的问题
        getBaicView().showBasicMenuLayout()
        getBaicView().showLoading()
        hideCustomView()
    }

    override fun reStart(reStartTime: Int?) {
        mVideoMgr?.reStart(reStartTime)
        post { resume() } //解决下一次设置资源后无法自动播放的问题
        getBaicView().showBasicMenuLayout()
        getBaicView().showLoading()
        hideCustomView()
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

    override fun getMaxPlayDuration(): Int {
        return mVideoMgr?.getMaxPlayDuration()?: 0
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

    override fun getSupportedBitrates(): List<BitrateItem>? {
        return mVideoMgr?.getSupportedBitrates()
    }

    override fun getCurrentBitrate(): BitrateItem? {
        return mVideoMgr?.getCurrentBitrate()
    }

    override fun setBitrate(bit: BitrateItem?) {
        getBaicView().updateBitrate(bit)
        mVideoMgr?.setBitrate(bit)
    }


    /************************************* 生命周期方法 *************************************/
    private var isBackgroundPlaying = false //是否允许后台播放
    private var isResumePlaying: Boolean? = null //切回界面后是否恢复播放
    private var mPauseBeforePlaying: Boolean? = null //切换界面之前的播放状态
    fun setBackgroundPlaying(isPlaying: Boolean){
        isBackgroundPlaying = isPlaying
    }
    fun setResumePlaying(isResume: Boolean){
        isResumePlaying = isResume
    }
    /**
     * 绑定生命周期，用以控制切换界面后视频是否任然播放
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
            if(mPauseBeforePlaying == true && isResumePlaying == true) resume()
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