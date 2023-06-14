package com.tdxtxt.liteavplayer.video.controller.view

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.ISeekBarController
import kotlin.math.max

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/8
 *     desc   :
 * </pre>
 */
class SeekBarControllerView : androidx.appcompat.widget.AppCompatSeekBar , ISeekBarController {
    private var mPlayerView: TXVideoPlayerView? = null
    private var mIsTrackingSeekBar = false//是否手指进行拖动
    private var isPlayOnTouchBefore: Boolean? = null //手指拖动前的播放状态
    private var mDragMaxProgress: Int? = null //允许手指拖动的最大进度值
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
                    val tempTrackMaxProgress = mDragMaxProgress
                    if(tempTrackMaxProgress != null && progress > tempTrackMaxProgress){
                        setProgress(tempTrackMaxProgress)
                    }else{
                        scrollValueProgress(progress)
                    }
                }else{
                    val tempTrackMaxProgress = mDragMaxProgress
                    if(tempTrackMaxProgress != null) {
                        mDragMaxProgress = max(tempTrackMaxProgress, progress)
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
        val nowTime = progress2Time(progress)
        mPlayerView?.getBaicView()?.updateTextTime(nowTime, null)
    }

    private fun progress2Time(progress: Int): Int{
        val percent = progress.toFloat() / max.toFloat()
        val totalTime = mPlayerView?.getDuration()?: 0
        val nowTime = (totalTime * percent).toInt()
        return nowTime
    }

    fun setTrackingTouchListener(trackingTouchListener: (isStart: Boolean) -> Unit){
        mTrackingTouchListener = trackingTouchListener
    }

    fun setDragMaxPercent(percent: Float?){
        if(percent == null){
            mDragMaxProgress = null
            return
        }
        val tempPercent = if(percent < 0) 0f else if(percent > 1) 1f else percent
        mDragMaxProgress = (max.toFloat() * tempPercent).toInt()
    }

    fun getDragMaxDuration(): Int? {
        val tempDragMaxProgress = mDragMaxProgress ?: return null
        val totalDuration = mPlayerView?.getDuration() ?: return null

        return (totalDuration * (tempDragMaxProgress.toFloat() / max.toFloat())).toInt()
    }

    /**
     * 拖动以前是播放状态的，拖动结束继续播放
     * 拖动以前是暂停状态的，拖动结束仍然是暂停状态
     * isTrackingSeekBar：true开始拖动,此时会有多次回调，false停止拖动
     */
    fun setTrackingSeekBar(isTrackingSeekBar: Boolean) {
        this.mIsTrackingSeekBar = isTrackingSeekBar
        if(isTrackingSeekBar){//开始拖动
            if(isPlayOnTouchBefore != null) return
            isPlayOnTouchBefore = mPlayerView?.isPlaying()
            if(isPlayOnTouchBefore == true) mPlayerView?.pause()
        }else{//停止拖动
            if(isPlayOnTouchBefore == true) mPlayerView?.resume()
            isPlayOnTouchBefore = null
            mPlayerView?.seekTo(progress2Time(progress))
        }
    }

    override fun attach(playerView: TXVideoPlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mPlayerView = null
    }


}