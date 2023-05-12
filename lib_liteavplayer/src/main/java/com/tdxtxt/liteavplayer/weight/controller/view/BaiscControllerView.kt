package com.tdxtxt.liteavplayer.weight.controller.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.weight.TXVideoPlayerView
import com.tdxtxt.liteavplayer.weight.inter.IBaiscController
import com.tencent.rtmp.ui.TXCloudVideoView
import kotlinx.android.synthetic.main.liteavlib_view_baisc_controller.view.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
class BaiscControllerView : FrameLayout, IBaiscController {
    private var mPlayerView: TXVideoPlayerView? = null
    private val mDefaultFadeTimeout = 7000L
    private val mFadeRunnable = Runnable { baisc_menu.visibility = View.GONE }

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }

    private fun initView(context: Context){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_baisc_controller, this, true)

        seekBar.setTrackingTouchListener { isStart ->
            if(isStart)  removeCallbacks(mFadeRunnable) else  postDelayed(mFadeRunnable, mDefaultFadeTimeout)
        }
        clickView(baisc_back)
        clickView(baisc_toggleplay)
        clickView(baisc_toggleplay_small)
        clickView(baisc_toggle_orient)
        clickView(baisc_backward)
        clickView(baisc_forward)
        clickView(baisc_multiple)

        updatePlayButton(!(mPlayerView?.isPlaying()?: false))
        showBaicMenuLayout()
    }

    fun getSeekBarControllerView(): SeekBarControllerView = seekBar

    private fun clickView(view: View?){
        view?.setOnClickListener {
            showBaicMenuLayout()
            when(it){
                baisc_back -> {
                    if(mPlayerView?.onBackPressed() == true){
                        mPlayerView?.release()
                        val activity = context
                        if(activity is Activity){
                            activity.finish()
                        }
                    }
                }
                baisc_toggleplay, baisc_toggleplay_small -> {
                    mPlayerView?.togglePlay()
                }
                baisc_toggle_orient -> {
                    if(mPlayerView?.isFullScreen() == true){
                        mPlayerView?.stopFullScreen()
                    }else{
                        mPlayerView?.startFullScreen()
                    }
                }
                baisc_backward -> {
                    val totalTime = mPlayerView?.getDuration()?: 0
                    var newTime = (mPlayerView?.getCurrentDuration()?: 0) - 15
                    if(newTime > totalTime) newTime = totalTime
                    if(newTime < 0) newTime = 0
                    updateTextTime(newTime, totalTime)
                    mPlayerView?.seekTo(newTime)
                }
                baisc_forward -> {
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
                baisc_multiple -> {
                    hideBaicMenuLayout()
                    mPlayerView?.getMultipleControllerView()?.show()
                }
            }
        }
    }

    fun setTrackingSeekBar(isTrackingSeekBar: Boolean){
        seekBar.setTrackingSeekBar(isTrackingSeekBar)
    }

    override fun setCoverIds(resId: Int) {
        baisc_cover.setImageResource(resId)
    }

    override fun getViewWidth(): Int {
        return width
    }

    override fun getViewHeight(): Int {
        return height
    }

    override fun showBaicMenuLayout() {
        removeCallbacks(mFadeRunnable)
        baisc_menu.visibility = View.VISIBLE
        postDelayed(mFadeRunnable, mDefaultFadeTimeout)
    }

    override fun hideBaicMenuLayout() {
        removeCallbacks(mFadeRunnable)
        mFadeRunnable.run()
    }

    override fun showLoading() {
        baisc_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        baisc_loading.visibility = View.GONE
    }

    override fun toggleBaicMenuLayout() {
        removeCallbacks(mFadeRunnable)
        if(baisc_menu.visibility == View.VISIBLE){
            hideBaicMenuLayout()
        }else{
            showBaicMenuLayout()
        }
    }

    override fun bindSurface() {
        mPlayerView?.getPlayer()?.setPlayerView(baisc_surface)
    }

    override fun unBindSurface() {
        mPlayerView?.getPlayer()?.setPlayerView(null as (TXCloudVideoView))
    }

    override fun updateNetspeed(speed: Int?) {
        baisc_netspeed.setText("${speed}kb/s")
    }

    override fun updatePlayButton(isPlaying: Boolean) {
        if(isPlaying){
            baisc_toggleplay_small.setImageResource(R.mipmap.liteavlib_ic_playing_small)
            baisc_toggleplay.setImageResource(R.mipmap.liteavlib_ic_playing)
        }else{
            baisc_toggleplay_small.setImageResource(R.mipmap.liteavlib_ic_pause_small)
            baisc_toggleplay.setImageResource(R.mipmap.liteavlib_ic_pause)
        }
    }

    override fun updateTextTime(current: Int?, total: Int?) {
        baisc_current_time.text = LiteavPlayerUtils.formatTime(current?: 0)
        if(total != null) baisc_total_time.text = LiteavPlayerUtils.formatTime(total)
    }

    override fun updateFullScreen(isFullScreen: Boolean?) {
        if(isFullScreen == true){
            baisc_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_small)
        }else{
            baisc_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_large)
        }
    }

    override fun updateSeekBar(progress: Int?, secondaryProgress: Int?) {
        if(progress != null) seekBar?.progress = (progress / 100f * seekBar.max).toInt()
        if(secondaryProgress != null) seekBar?.secondaryProgress = (secondaryProgress / 100f * seekBar.max).toInt()
    }

    override fun attach(playerView: TXVideoPlayerView) {
        mPlayerView = playerView
        playerView.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        seekBar.attach(playerView)
    }

    override fun detach() {
        baisc_surface?.onDestroy()
        seekBar.detach()
    }

}