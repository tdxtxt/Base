package com.tdxtxt.liteavplayer.video.controller

import android.app.Service
import android.content.Context
import android.media.AudioManager
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tdxtxt.liteavplayer.video.inter.IPlayerController
import com.tdxtxt.liteavplayer.video.inter.TXPlayerListener

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/1
 *     desc   : 音频焦点控制、耳机控制
 * </pre>
 */
class PlayerAudioFocusController : IPlayerController {
    private var mVideoMgr: VideoMananger? = null
    private var mContext: Context? = null
    private var afChangeListener: AudioManager.OnAudioFocusChangeListener? = null
    //当前音频焦点的状态
    private var mCurrentAudioFocusState: Int? = null

    override fun attach(context: Context?, videoMgr: VideoMananger?) {
        this.mVideoMgr = videoMgr
        this.mContext = context
    }

    override fun detach() {
        unregisterAudioChangeListener()
    }

    override fun onPlayStateChanged(state: Int, value: Any?) {
        if(state == TXPlayerListener.PlayerState.EVENT_START){
            registerAudioChangeListener()
        }
    }

    private fun registerAudioChangeListener(){
        if(afChangeListener == null)
            afChangeListener = AudioManager.OnAudioFocusChangeListener {
                when(it){
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        // 失去焦点短时间，暂停播放
                        mVideoMgr?.pause()
                    }

                    AudioManager.AUDIOFOCUS_LOSS -> {
                        // 失去焦点很长时间，释放音频资源
                        mVideoMgr?.pause()
                        unregisterAudioChangeListener()
                    }

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        // 短时间失去焦点，降低音量
                        mVideoMgr?.pause()
                    }

                    AudioManager.AUDIOFOCUS_GAIN -> {
                        //申请得到的音频焦点不知道会持续多久，一般是长期占有
                    }
                }
            }
        mCurrentAudioFocusState = getAudioManager()?.requestAudioFocus(
            afChangeListener, AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    private fun unregisterAudioChangeListener(){
        if(afChangeListener != null){
            getAudioManager()?.abandonAudioFocus(afChangeListener)
            afChangeListener = null
        }
    }

    private fun getAudioManager() = mContext?.getSystemService(Service.AUDIO_SERVICE) as AudioManager?

}