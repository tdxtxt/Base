package com.tdxtxt.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.tdxtxt.video.R
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.IControllerWrapper
import com.tdxtxt.video.utils.PlayerUtils
import kotlinx.android.synthetic.main.libvideo_view_control_wrapper.view.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 基础播放视图【封面图、播放/暂停按钮、进度条、返回按钮、标题】
 * </pre>
 */
class ControlWrapperView : FrameLayout,
    IControllerWrapper {
    private var mContainer: VideoPlayerView? = null
    private val mDefaultFadeTimeout = 5000L
    private var mIsTrackingSeekBar = false
    private val mFadeRunnable = Runnable { wrapper_menu.visibility = View.GONE }

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_control_wrapper, this, true)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(skb: SeekBar?, progress: Int, fromUser: Boolean) {
                scrollValueProgress(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                removeCallbacks(mFadeRunnable)
                mIsTrackingSeekBar = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                postDelayed(mFadeRunnable, mDefaultFadeTimeout)
                mIsTrackingSeekBar = false
            }
        })

        clickView(wrapper_back)
        clickView(wrapper_toggleplay)
        clickView(wrapper_toggleplay_small)
        clickView(wrapper_toggle_orient)
        clickView(wrapper_backward)
        clickView(wrapper_forward)
        clickView(wrapper_multiple)

        updateTogglePlay(mContainer?.isPlaying() == true)
        toggleMenu()
    }

    private fun scrollValueProgress(progress: Int){
        val rate = progress.toFloat() / seekBar.max.toFloat()
        val totalTime = mContainer?.getDuration()?: 0
        val nowTime = totalTime * rate
        scrollTimeProgress(nowTime.toLong())
    }

    private fun scrollTimeProgress(time: Long){
        mContainer?.seekTo(time)
        if(mIsTrackingSeekBar){
            changeProgress(time)
        }
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            showMenu()

            when(it){
                wrapper_back -> {
                    mContainer?.onBackPressed()
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

    override fun scrollSeekBar(time: Long){
        val totalTime = mContainer?.getDuration()?: 0
        val rate = time.toFloat() / totalTime.toFloat()
        val progress = seekBar.max * rate
        seekBar.progress = progress.toInt()
        changeProgress(time)
        showMenu()
    }

    override fun toggleMenu(){
        removeCallbacks(mFadeRunnable)
        if(wrapper_menu.visibility == View.VISIBLE){
            hideMenu()
        }else{
            showMenu()
        }
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

    override fun getViewWidth(): Int {
        return width
    }

    override fun getViewHeight(): Int {
        return height
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mContainer?.getVideoPlayer()?.bindSurface(wrapper_surface)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mContainer?.getVideoPlayer()?.unbindSurface()
    }

    override fun attach(container: VideoPlayerView) {
        this.mContainer = container
        container.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun detach() {

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

    override fun changeVideoSize(widthSize: Int, heightSize: Int) {
        wrapper_surface.setVideoSize(widthSize, heightSize)
    }

    override fun changeMultiple(value: Float) {
        wrapper_multiple.text = PlayerUtils.formatMultiple(value)
    }

    override fun changeProgress(current: Long) {
        wrapper_current_time.text = PlayerUtils.formatTime(current)
    }

    override fun updateTime(current: Long, total: Long) {
        wrapper_current_time.text = PlayerUtils.formatTime(current)
        wrapper_total_time.text = PlayerUtils.formatTime(total)
    }

}