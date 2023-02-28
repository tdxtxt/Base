package com.tdxtxt.video

import android.content.Context
import android.os.SystemClock
import android.util.ArrayMap
import com.tdxtxt.video.kernel.impl.exo.ExoMediaPlayer
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
        managerMap.remove(id)
    }


    companion object{
        var CONTEXT: Context? = null
        val managerMap = ArrayMap<Long, VideoPlayerManager>()

        fun init(context: Context){
            CONTEXT = context.applicationContext
        }

        fun newInstance(): VideoPlayerManager{
            val instance = VideoPlayerManager(SystemClock.uptimeMillis(), ExoMediaPlayer(CONTEXT!!))
            managerMap.put(instance.id, instance)
            return instance
        }
    }
}