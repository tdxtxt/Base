package com.tdxtxt.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.tdxtxt.video.R
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.utils.PlayerConstant
import com.tdxtxt.video.utils.PlayerUtils
import kotlinx.android.synthetic.main.libvideo_view_baise.view.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 基础播放视图【封面图、播放/暂停按钮、进度条、返回按钮、标题】
 * </pre>
 */
class BaiseControllerView : FrameLayout, IBaiseController {
    private var mVideoPlayer: AbstractVideoPlayer? = null
    private val mDefaultFadeTimeout = 5000L
    private var mIsTrackingSeekBar = false;
    private val mFadeRunnable = Runnable { baise_menu.visibility = View.GONE }

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_baise, this, true)

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

        clickView(baise_switch_small)
        clickView(baise_switch)

        if(mVideoPlayer?.isPlaying() == true){
            updatePauseUI()
        }else{
            updateStartUI()
        }
        toggleMenu()
    }

    private fun scrollValueProgress(progress: Int){
        val rate = progress.toFloat() / seekBar.max.toFloat()
        val totalTime = mVideoPlayer?.getDuration()?: 0
        val nowTime = totalTime * rate
        scrollTimeProgress(nowTime.toLong())
    }

    private fun scrollTimeProgress(time: Long){
        mVideoPlayer?.seekTo(time)
        if(mIsTrackingSeekBar){
            changeProgress(time)
        }
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            when(it){
                baise_switch, baise_switch_small ->{
                    if(mVideoPlayer?.isPlaying() == true){
                        mVideoPlayer?.pause()
                    }else{
                        mVideoPlayer?.start()
                    }
                }
            }
        }
    }

    override fun scrollSeekBar(time: Long){
        val totalTime = mVideoPlayer?.getDuration()?: 0
        val rate = time.toFloat() / totalTime.toFloat()
        val progress = seekBar.max * rate
        seekBar.progress = progress.toInt()
        changeProgress(time)
        showMenu()
    }

    override fun toggleMenu(){
        removeCallbacks(mFadeRunnable)
        if(baise_menu.visibility == View.VISIBLE){
            hideMenu()
        }else{
            showMenu()
        }
    }

    override fun showMenu(){
        removeCallbacks(mFadeRunnable)
        baise_menu.visibility = View.VISIBLE
        postDelayed(mFadeRunnable, mDefaultFadeTimeout)
    }

    override fun hideMenu(){
        removeCallbacks(mFadeRunnable)
        mFadeRunnable.run()
    }

    override fun getView(): View {
        return this
    }

    override fun getViewWidth(): Int {
        return width
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mVideoPlayer?.bindSurface(baise_surface)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mVideoPlayer?.unbindSurface()
    }

    override fun attachPlayer(videoPlayer: AbstractVideoPlayer) {
        this.mVideoPlayer = videoPlayer
    }

    override fun updateStartUI() {
        baise_switch_small.setImageResource(R.mipmap.libvideo_ic_playing_small)
        baise_switch.setImageResource(R.mipmap.libvideo_ic_playing)
        baise_switch.setTag(PlayerConstant.EVENT_PASUE)
    }

    override fun updatePauseUI() {
        baise_switch_small.setImageResource(R.mipmap.libvideo_ic_pause_small)
        baise_switch.setImageResource(R.mipmap.libvideo_ic_pause)
        baise_switch.setTag(PlayerConstant.EVENT_PLAYING)
    }

    override fun updateCover(resId: Int) {
        baise_cover.setImageResource(resId)
    }

    override fun updateBufferProgress(rate: Float) {
        baise_buffer.visibility = if(rate < 1) View.VISIBLE else View.GONE
    }

    override fun changeVideoSize(widthSize: Int, heightSize: Int) {
        baise_surface.setVideoSize(widthSize, heightSize)
    }

    override fun changeProgress(current: Long) {
        tv_current_time.text = PlayerUtils.formatTime(current)
    }

    override fun updateTime(current: Long, total: Long) {
        tv_current_time.text = PlayerUtils.formatTime(current)
        tv_total_time.text = PlayerUtils.formatTime(total)
    }

}