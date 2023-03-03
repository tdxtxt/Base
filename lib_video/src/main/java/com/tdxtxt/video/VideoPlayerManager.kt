package com.tdxtxt.video

import android.content.Context
import android.os.SystemClock
import android.util.ArrayMap
import com.tdxtxt.video.kernel.impl.exo.ExoMediaPlayer
import com.tdxtxt.video.kernel.impl.media.AndroidMediaPlayer
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.kernel.inter.VideoPlayerListener
import com.tdxtxt.video.utils.PlayerUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
class VideoPlayerManager(val id: Long, val player: AbstractVideoPlayer) : IVideoPlayer by player{

    fun getVideoPlayer(): AbstractVideoPlayer{
        return player
    }

    fun addPlayerEventListener(listener: VideoPlayerListener?){
        if(listener == null) return

        player.addPlayerEventListener(listener)
    }

    override fun release() {
        player.release()
        mInstance = null
    }


    companion object{
        private var mInstance: VideoPlayerManager? = null

        fun newInstance(context: Context? = PlayerUtils.getApplicationByReflect()): VideoPlayerManager{
            if(mInstance == null || mInstance?.isRelease() == true){
                mInstance = VideoPlayerManager(SystemClock.uptimeMillis(), ExoMediaPlayer(context))
            }
            return mInstance!!
        }
    }
}