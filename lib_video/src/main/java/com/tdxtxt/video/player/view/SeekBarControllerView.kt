package com.tdxtxt.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.IControllerSeekBar
import kotlin.math.max

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/8
 *     desc   :
 * </pre>
 */
class SeekBarControllerView : androidx.appcompat.widget.AppCompatSeekBar , IControllerSeekBar {
    private var mContainer: VideoPlayerView? = null
    private var mIsTrackingSeekBar = false//是否手指进行拖动
    private var isPlayingTrackingSeekBar: Boolean? = null //手指拖动前的播放状态
    private var mTrackMaxProgress: Int? = null //允许手指拖动的最大进度值
    private var mTrackingTouchListener: ((isStart: Boolean) -> Unit)? = null


    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }

    private fun initView(context: Context){
        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(skb: SeekBar?, progress: Int, fromUser: Boolean) {
                if(mIsTrackingSeekBar){
                    if(mTrackMaxProgress != null && progress > mTrackMaxProgress!!){
                        setProgress(mTrackMaxProgress!!)
                    }else{
                        scrollValueProgress(progress)
                    }
                }else{
                    val tempTrackMaxProgress = mTrackMaxProgress
                    if(tempTrackMaxProgress != null) {
                        mTrackMaxProgress = max(tempTrackMaxProgress, progress)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mTrackingTouchListener?.invoke(true)
                setTrackingSeekBar(true)
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mTrackingTouchListener?.invoke(false)
                setTrackingSeekBar(false)
            }
        })
    }

    private fun scrollValueProgress(progress: Int){
        val percent = progress.toFloat() / max.toFloat()
        val totalTime = mContainer?.getDuration()?: 0
        val nowTime = (totalTime * percent).toLong()
        mContainer?.seekTo(nowTime)
        mContainer?.getControlWrapper()?.updateTime(nowTime, null)
    }

    fun setTrackingTouchListener(trackingTouchListener: (isStart: Boolean) -> Unit){
        mTrackingTouchListener = trackingTouchListener
    }

    fun setTrackMaxPercent(percent: Float){
        val tempPercent = if(percent < 0) 0f else if(percent > 1) 1f else percent
        mTrackMaxProgress = (max.toFloat() * tempPercent).toInt()
    }

    fun getTrackMaxDuration(): Long? {
        val tempTrackMaxProgress = mTrackMaxProgress ?: return null
        val totalDuration = mContainer?.getVideoPlayer()?.getDuration() ?: return null

        return (totalDuration * (tempTrackMaxProgress.toFloat() / max.toFloat())).toLong()
    }

    /**
     * 拖动以前是播放状态的，拖动结束继续播放
     * 拖动以前是暂停状态的，拖动结束仍然是暂停状态
     * isTrackingSeekBar：true开始拖动，false停止拖动
     */
    fun setTrackingSeekBar(isTrackingSeekBar: Boolean) {
        this.mIsTrackingSeekBar = isTrackingSeekBar
        if(isPlayingTrackingSeekBar == isTrackingSeekBar) return
        if(isTrackingSeekBar){//开始拖动
            isPlayingTrackingSeekBar = mContainer?.isPlaying()
            if(isPlayingTrackingSeekBar == true) mContainer?.pause()
        }else{
            if(isPlayingTrackingSeekBar == true) mContainer?.start()
            isPlayingTrackingSeekBar = null
        }
    }

    override fun attach(container: VideoPlayerView) {
        mContainer = container
    }

    override fun detach() {

    }
}