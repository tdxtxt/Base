package com.tdxtxt.video.player.view

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.tdxtxt.video.R
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.IControllerGesture

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   : 声音调节
 * </pre>
 */
class VolumeControllerView: FrameLayout, IControllerGesture {
    private var mContainer: VideoPlayerView? = null
    private var mAudioManager: AudioManager? = null
    private var mStreamVolume: Int = 0

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_control_volume_bright, this, true)
        findViewById<ImageView>(R.id.iv_volume_bright)?.setImageResource(R.mipmap.libvideo_ic_volume)
    }

    override fun show(changePercent: Float) {
        val parentView = parent
        if(!(parentView is ViewGroup)){
            this.mContainer?.getControlWrapper()?.addView(this)
        }

        if(mAudioManager == null) mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if(mStreamVolume == 0) mStreamVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?: 0
        val streamMaxVolume = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?: 0
        var newVolume = (mStreamVolume + streamMaxVolume * changePercent).toInt()
        if(newVolume < 0) newVolume = 0
        if(newVolume > streamMaxVolume) newVolume = streamMaxVolume
        findViewById<ImageView>(R.id.iv_volume_bright)?.setImageResource(if(newVolume == 0) R.mipmap.libvideo_ic_volume_off else R.mipmap.libvideo_ic_volume)
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND)

        val progress = if(streamMaxVolume == 0) 0 else (newVolume.toFloat() / streamMaxVolume * 100).toInt()
        findViewById<ProgressBar>(R.id.progress_volume_bright)?.progress = progress
    }

    override fun hide() {
        mStreamVolume = 0
        val parentView = parent
        if(parentView is ViewGroup){
            parentView.removeView(this)
        }
    }

    override fun attach(container: VideoPlayerView) {
        this.mContainer = container
    }

    override fun detach() {
    }
}