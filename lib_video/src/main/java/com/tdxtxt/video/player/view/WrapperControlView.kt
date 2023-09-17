package com.tdxtxt.video.player.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.video.R
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.IControllerWrapper
import com.tdxtxt.video.utils.PlayerUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 基础播放视图【封面图、播放/暂停按钮、进度条、返回按钮、标题】
 * </pre>
 */
class WrapperControlView : FrameLayout,
    IControllerWrapper {


    private var mContainer: VideoPlayerView? = null
    private val mDefaultFadeTimeout = 7000L
    private val mFadeRunnable = Runnable { wrapper_menu.visibility = View.GONE }

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_control_wrapper, this, true)
        if(isInEditMode) setBackgroundResource(android.R.color.black)
        seekBar.setTrackingTouchListener { isStart ->
            if(isStart)  removeCallbacks(mFadeRunnable) else  postDelayed(mFadeRunnable, mDefaultFadeTimeout)
        }

        clickView(wrapper_back)
        clickView(wrapper_toggleplay)
        clickView(wrapper_toggleplay_small)
        clickView(wrapper_toggle_orient)
        clickView(wrapper_backward)
        clickView(wrapper_forward)
        clickView(wrapper_multiple)

        updateTogglePlay(mContainer?.isPlaying() == true)
        if(!isInEditMode){
            toggleMenu()
        }
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            showMenu()

            when(it){
                wrapper_back -> {
                    if(mContainer?.onBackPressed() == true){
                        mContainer?.release()
                        val activity = context
                        if(activity is Activity){
                            activity.finish()
                        }
                    }
                }
                wrapper_toggleplay_small, wrapper_toggleplay ->{
                    mContainer?.togglePlay()
                }
                wrapper_toggle_orient -> {
                    if(mContainer?.isFullScreen() == true){
                        mContainer?.stopFullScreen()
                    }else{
                        mContainer?.startFullScreen()
                    }
                }
                wrapper_backward -> {
                    val totalTime = mContainer?.getDuration()?: 0L
                    var newTime = (mContainer?.getCurrentDuration()?: 0L) - 15000L
                    if(newTime > totalTime) newTime = totalTime
                    if(newTime < 0) newTime = 0L
                    updateTime(newTime, totalTime)
                    mContainer?.seekTo(newTime)
                }
                wrapper_forward -> {
                    val totalTime = mContainer?.getDuration()?: 0L
                    var newTime = (mContainer?.getCurrentDuration()?: 0L) + 15000L
                    val trackMaxDuration = seekBar.getTrackMaxDuration()
                    if(trackMaxDuration != null && newTime > trackMaxDuration){
                        newTime = trackMaxDuration
                    }
                    if(newTime > totalTime) newTime = totalTime
                    if(newTime < 0) newTime = 0L
                    updateTime(newTime, totalTime)
                    mContainer?.seekTo(newTime)
                }

                wrapper_multiple -> {
                    hideMenu()
                    mContainer?.getMultipleContronller()?.show()
                }
            }
        }
    }

    fun setTrackMaxPercent(trackMaxPercent: Float){
        seekBar.setTrackMaxPercent(trackMaxPercent)
    }

    override fun toggleMenu(){
        removeCallbacks(mFadeRunnable)
        if(wrapper_menu.visibility == View.VISIBLE){
            hideMenu()
        }else{
            showMenu()
        }
    }

    override fun setTrackingSeekBar(isTrackingSeekBar: Boolean) {
        seekBar.setTrackingSeekBar(isTrackingSeekBar)
    }

    override fun showMenu(){
        removeCallbacks(mFadeRunnable)
        wrapper_menu.visibility = View.VISIBLE
        postDelayed(mFadeRunnable, mDefaultFadeTimeout)
    }

    override fun hideMenu(){
        removeCallbacks(mFadeRunnable)
        mFadeRunnable.run()
    }

    override fun bindSurface() {
        mContainer?.getVideoPlayer()?.bindSurface(wrapper_surface)
    }

    override fun unBindSurface() {
        mContainer?.getVideoPlayer()?.unbindSurface()
    }

    override fun getViewWidth(): Int {
        return width
    }

    override fun getViewHeight(): Int {
        return height
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindSurface()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unBindSurface()
    }

    override fun attach(container: VideoPlayerView) {
        this.mContainer = container
        container.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        seekBar.attach(container)
    }

    override fun detach() {
        unBindSurface()
        seekBar.detach()
    }

    override fun updateTogglePlay(isPlaying: Boolean) {
        if(isPlaying){
            wrapper_toggleplay_small.setImageResource(R.mipmap.libvideo_ic_playing_small)
            wrapper_toggleplay.setImageResource(R.mipmap.libvideo_ic_playing)
        }else{
            wrapper_toggleplay_small.setImageResource(R.mipmap.libvideo_ic_pause_small)
            wrapper_toggleplay.setImageResource(R.mipmap.libvideo_ic_pause)
        }
    }

    override fun updateFullScreen(isFullScreen: Boolean?) {
        if(isFullScreen == true){
            wrapper_toggle_orient.setImageResource(R.mipmap.libvideo_ic_orient_small)
        }else{
            wrapper_toggle_orient.setImageResource(R.mipmap.libvideo_ic_orient_large)
        }
    }

    override fun updateCover(resId: Int) {
        wrapper_cover.setImageResource(resId)
    }

    override fun updateBufferProgress(rate: Float) {
        wrapper_buffer.visibility = if(rate < 1) View.VISIBLE else View.GONE
    }

    override fun changeVideoSize(widthSize: Int?, heightSize: Int?) {
        wrapper_surface.setVideoSize(widthSize, heightSize)
    }

    override fun updateMultiple(value: Float) {
        wrapper_multiple.text = PlayerUtils.formatMultiple(value)
    }

    override fun updateTime(current: Long?, total: Long?) {
        wrapper_current_time.text = PlayerUtils.formatTime(current?: 0)
        if(total != null) wrapper_total_time.text = PlayerUtils.formatTime(total)
    }

    override fun updateSeekBar(progress: Int?, secondaryProgress: Int?) {
        if(progress != null) seekBar?.progress = (progress / 100f * seekBar.max).toInt()
        if(secondaryProgress != null) seekBar?.secondaryProgress = (secondaryProgress / 100f * seekBar.max).toInt()
    }

}