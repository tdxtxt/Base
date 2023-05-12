package com.tdxtxt.video

import android.os.SystemClock
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.kernel.inter.VideoPlayerListener

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

        fun newInstance(player: AbstractVideoPlayer): VideoPlayerManager{
            if(mInstance == null || mInstance?.isRelease() == true){
                mInstance = VideoPlayerManager(SystemClock.uptimeMillis(), player)
            }
            return mInstance!!
        }
    }
}