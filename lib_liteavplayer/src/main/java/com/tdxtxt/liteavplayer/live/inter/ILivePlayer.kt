package com.tdxtxt.liteavplayer.live.inter

import com.tencent.live2.V2TXLivePlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   :
 * </pre>
 */
interface ILivePlayer {
    fun getPlayer(): V2TXLivePlayer?

    /**
     * 开始直播，进度从当前播放停留的进度开始
     */
    fun setLiveSource(url: String?)
    /**
     * 恢复播放
     */
    fun resume()

    /**
     * 暂停
     */
    fun pause()

    /**
     * 释放播放器
     */
    fun release()
}