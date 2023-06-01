package com.tdxtxt.liteavplayer.video.controller

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tdxtxt.liteavplayer.video.inter.IPlayerController


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/1
 *     desc   : 耳机控制
 * </pre>
 */
class PlayerHeadsetController : BroadcastReceiver(), IPlayerController {
    private var mVideoMgr: VideoMananger? = null
    private var mContext: Context? = null
    private var mediaComponentName: ComponentName? = null
    private var mediaSession: MediaSessionCompat? = null

    override fun attach(context: Context?, videoMgr: VideoMananger?) {
        this.mVideoMgr = videoMgr
        this.mContext = context

        registerButtonEventListener()
    }

    override fun detach() {
        unregisterButtonEventListener()
    }

    override fun onPlayStateChanged(state: Int, value: Any?) {

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        when(action){
            Intent.ACTION_MEDIA_BUTTON -> {
                val event: KeyEvent? = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                val eventAction = event?.action
                if(eventAction == KeyEvent.ACTION_DOWN){
                    val keycode = event.keyCode
                    if(keycode == KeyEvent.KEYCODE_HEADSETHOOK){
                        mVideoMgr?.togglePlay()
                    }else if(keycode == KeyEvent.KEYCODE_MEDIA_PAUSE){
                        if(mVideoMgr?.isPlaying() == true) mVideoMgr?.pause()
                    }else if(keycode == KeyEvent.KEYCODE_MEDIA_PLAY){
                        if(mVideoMgr?.isPlaying() == false) mVideoMgr?.resume()
                    }
                }
            }
        }
    }

    private fun registerButtonEventListener(){
        val context = mContext?: return
        mediaComponentName = ComponentName(context.packageName, PlayerHeadsetController::class.java.name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0以上
            if(mediaSession != null) return
            mediaSession = MediaSessionCompat(context, "TAG", mediaComponentName, null)
            mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            mediaSession?.setCallback(object : MediaSessionCompat.Callback(){
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    onReceive(context, mediaButtonEvent)
                    return true
                }
            }, Handler(Looper.getMainLooper()))
            mediaSession?.isActive = true
        }else{
            try {
                getAudioManager()?.registerMediaButtonEventReceiver(mediaComponentName)
            } catch (e: Exception) {}
        }
    }

    private fun unregisterButtonEventListener(){
        try {
            getAudioManager()?.unregisterMediaButtonEventReceiver(mediaComponentName)
            mediaComponentName = null
        } catch (e: Exception) {}
        mediaSession?.setCallback(null)
        mediaSession?.release()
        mediaSession = null
    }

    private fun getAudioManager() = mContext?.getSystemService(Service.AUDIO_SERVICE) as AudioManager?
}