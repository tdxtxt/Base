package com.tdxtxt.liteavplayer.video.controller.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.IBasicController
import com.tencent.rtmp.ui.TXCloudVideoView
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_video.view.*
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
class BasicControllerView : FrameLayout, IBasicController {
    private var mPlayerView: TXVideoPlayerView? = null
    private val mDefaultFadeTimeout = 7000L
    private var mLastBasicMenuLayoutShowTime = 0L
    private val mFadeBasicMenuLayoutRunnable = Runnable {
        basic_menu.visibility = View.GONE
        mPlayerView?.getMultipleControllerView()?.hide()
    }

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }

    private fun initView(context: Context){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_basic_controller_video, this, true)

        seekBar.setTrackingTouchListener { isStart ->
            if(isStart)  removeCallbacks(mFadeBasicMenuLayoutRunnable) else  postDelayed(mFadeBasicMenuLayoutRunnable, mDefaultFadeTimeout)
        }
        clickView(basic_back)
        clickView(basic_toggleplay)
        clickView(basic_toggleplay_small)
        clickView(basic_toggle_orient)
        clickView(basic_backward)
        clickView(basic_forward)
        clickView(basic_multiple)

        updatePlayButton(!(mPlayerView?.isPlaying()?: false))
        showBasicMenuLayout()
    }

    fun getSeekBarControllerView(): SeekBarControllerView = seekBar

    fun getMultiplePlaceHolder() = basic_multiple_placeholder

    fun setTitle(title: CharSequence?){
        basic_back.text = title
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            showBasicMenuLayout()
            when(it){
                basic_back -> {
                    mPlayerView?.back()
                }
                basic_toggleplay, basic_toggleplay_small -> {
                    mPlayerView?.togglePlay()
                }
                basic_toggle_orient -> {
                    if(mPlayerView?.isFullScreen() == true){
                        mPlayerView?.stopFullScreen()
                    }else{
                        mPlayerView?.startFullScreen()
                    }
                }
                basic_backward -> {
                    val totalTime = mPlayerView?.getDuration()?: 0
                    var newTime = (mPlayerView?.getCurrentDuration()?: 0) - 15
                    if(newTime > totalTime) newTime = totalTime
                    if(newTime < 0) newTime = 0
                    updateTextTime(newTime, totalTime)
                    mPlayerView?.seekTo(newTime)
                }
                basic_forward -> {
                    val totalTime = mPlayerView?.getDuration()?: 0
                    var newTime = (mPlayerView?.getCurrentDuration()?: 0) + 15
                    val trackMaxDuration = seekBar.getTrackMaxDuration()
                    if(trackMaxDuration != null && newTime > trackMaxDuration){
                        newTime = trackMaxDuration
                    }
                    if(newTime > totalTime) newTime = totalTime
                    if(newTime < 0) newTime = 0
                    updateTextTime(newTime, totalTime)
                    mPlayerView?.seekTo(newTime)
                }
                basic_multiple -> {
                    mPlayerView?.getMultipleControllerView()?.toggle()
                }
            }
        }
    }

    fun setTrackingSeekBar(isTrackingSeekBar: Boolean){
        seekBar.setTrackingSeekBar(isTrackingSeekBar)
    }

    fun setWaterMark(dynamicWatermarkTip: String?, tipTextSize: Int, tipTextColor: Int){
        waterMark.setData(dynamicWatermarkTip, tipTextSize, tipTextColor)
        waterMark.show()
    }

    override fun setCoverIds(resId: Int) {
        basic_cover.setImageResource(resId)
    }

    override fun getViewWidth(): Int {
        return width
    }

    override fun getViewHeight(): Int {
        return height
    }

    override fun showBasicMenuLayout() {
        if(abs(System.currentTimeMillis() - mLastBasicMenuLayoutShowTime) > 800){
            mLastBasicMenuLayoutShowTime = System.currentTimeMillis()
            removeCallbacks(mFadeBasicMenuLayoutRunnable)
            basic_menu.visibility = View.VISIBLE
            postDelayed(mFadeBasicMenuLayoutRunnable, mDefaultFadeTimeout)
        }
    }

    override fun hideBasicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        mFadeBasicMenuLayoutRunnable.run()
    }

    override fun showLoading() {
        basic_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        basic_loading.visibility = View.GONE
    }

    override fun toggleBaicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        if(basic_menu.visibility == View.VISIBLE){
            hideBasicMenuLayout()
        }else{
            showBasicMenuLayout()
        }
    }

    override fun bindSurface() {
        mPlayerView?.getPlayer()?.setPlayerView(basic_surface)
    }

    override fun unBindSurface() {
        mPlayerView?.getPlayer()?.setPlayerView(null as (TXCloudVideoView))
    }

    override fun updateNetspeed(speed: Int?) {
        basic_netspeed.setText(LiteavPlayerUtils.formatSpeed(speed))
    }

    override fun updatePlayButton(isPlaying: Boolean) {
        if(isPlaying){
            basic_toggleplay_small.setImageResource(R.mipmap.liteavlib_ic_playing_small)
            basic_toggleplay.setImageResource(R.mipmap.liteavlib_ic_playing)
        }else{
            basic_toggleplay_small.setImageResource(R.mipmap.liteavlib_ic_pause_small)
            basic_toggleplay.setImageResource(R.mipmap.liteavlib_ic_pause)
        }
    }

    override fun updateTextTime(current: Int?, total: Int?) {
        basic_current_time.text = LiteavPlayerUtils.formatTime(current?: 0)
        if(total != null) basic_total_time.text = LiteavPlayerUtils.formatTime(total)
    }

    override fun updateFullScreen(isFullScreen: Boolean?) {
        if(isFullScreen == true){
            basic_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_small)
        }else{
            basic_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_large)
        }
    }

    override fun updateSeekBar(progress: Int?, secondaryProgress: Int?) {
        if(progress != null) seekBar?.progress = (progress / 100f * seekBar.max).toInt()
        if(secondaryProgress != null) seekBar?.secondaryProgress = (secondaryProgress / 100f * seekBar.max).toInt()
    }

    override fun updateMultiple(value: Float) {
        basic_multiple.text =  LiteavPlayerUtils.formatMultiple(value)
    }

    override fun attach(playerView: TXVideoPlayerView) {
        mPlayerView = playerView
        playerView.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        seekBar.attach(playerView)
    }

    override fun detach() {
        basic_surface?.onDestroy()
        seekBar.detach()
        waterMark.release()
    }

    override fun onPause() {
        waterMark.hide()
    }

    override fun onResume() {
        waterMark.show()
    }

}