package com.tdxtxt.liteavplayer.live.controller.view

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IGestureController
import kotlinx.android.synthetic.main.liteavlib_view_control_volume_bright.view.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   : 声音调节
 * </pre>
 */
class VolumeControllerView: FrameLayout, IGestureController {
    private var mPlayerView: TXLivePlayerView? = null
    private var mAudioManager: AudioManager? = null
    private var mStreamVolume: Int = 0

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_control_volume_bright, this, true)
        iv_volume_bright.setImageResource(R.mipmap.liteavlib_ic_volume)
    }

    override fun show(changePercent: Float) {
        val parentView = parent
        if(!(parentView is ViewGroup)){
            this.mPlayerView?.getBaicView()?.addView(this)
        }

        if(mAudioManager == null) mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if(mStreamVolume == 0) mStreamVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?: 0
        val streamMaxVolume = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?: 0
        var newVolume = (mStreamVolume + streamMaxVolume * changePercent).toInt()
        if(newVolume < 0) newVolume = 0
        if(newVolume > streamMaxVolume) newVolume = streamMaxVolume
        iv_volume_bright.setImageResource(if(newVolume == 0) R.mipmap.liteavlib_ic_volume_off else R.mipmap.liteavlib_ic_volume)
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND)

        val progress = if(streamMaxVolume == 0) 0 else (newVolume.toFloat() / streamMaxVolume * 100).toInt()
        progress_volume_bright.progress = progress
    }

    override fun hide() {
        mStreamVolume = 0
        val parentView = parent
        if(parentView is ViewGroup){
            parentView.removeView(this)
        }
    }

    override fun attach(playerView: TXLivePlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mPlayerView = null
        this.mAudioManager = null
    }
}