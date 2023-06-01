package com.tdxtxt.liteavplayer.live

import android.content.Context
import com.tdxtxt.liteavplayer.live.inter.ILivePlayer
import com.tencent.live2.V2TXLivePlayer
import com.tencent.live2.impl.V2TXLivePlayerImpl

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   :
 * </pre>
 */
class LiveMananger constructor(val context: Context?, val id: Long): ILivePlayer {
    private var mPlayer: V2TXLivePlayer? = null
    init {
        mPlayer = V2TXLivePlayerImpl(context)
        configPlayer(mPlayer)
    }

    private fun configPlayer(livePlayer: V2TXLivePlayer?){
        livePlayer?.setCacheParams(1.0f, 5.0f)
    }

    override fun getPlayer(): V2TXLivePlayer? {
        return mPlayer
    }

    override fun setLiveSource(url: String?) {
        getPlayer()?.startLivePlay(url)
    }

    override fun resume() {
        getPlayer()?.resumeAudio()
        getPlayer()?.resumeAudio()
    }

    override fun pause() {
        getPlayer()?.pauseAudio()
        getPlayer()?.pauseVideo()
    }

    override fun release() {
        getPlayer()?.stopPlay()
    }
}