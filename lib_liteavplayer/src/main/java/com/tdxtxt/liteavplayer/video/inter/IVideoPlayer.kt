package com.tdxtxt.liteavplayer.video.inter

import android.content.Context
import com.tencent.rtmp.TXVodPlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/12
 *     desc   :
 * </pre>
 */
interface IVideoPlayer {
    fun getPlayer(): TXVodPlayer?

    /*----------------------------第一部分：视频播放器----------------------------------*/
    fun setToken(token: String?)
    fun addPlayerEventListener(listener: TXPlayerListener?)
    fun removeEventListener(listener: TXPlayerListener?)

    /*----------------------------第二部分：视频播放器状态方法----------------------------------*/
    /**
     * 播放加密视频
     */
    fun setFileIdSource(appId: Int, fileId: String?, psign: String?, startTime: Int? = null, autoPlay: Boolean = true)
    /**
     * 播放，进度从当前播放停留的进度开始
     */
    fun setDataSource(path: String?, startTime: Int? = null, autoPlay: Boolean = true)
    /**
     * 获取播放url
     */
    fun getDataSource(): String?
    /**
     * 重新播放，进度从0开始
     */
    fun reStart()

    /**
     * 恢复播放
     */
    fun resume()

    /**
     * 暂停
     */
    fun pause()

    /**
     * @clearFrame 清除画面残留的帧
     * 停止播放
     */
    fun stop(clearFrame: Boolean = true)

    /**
     * 暂停/播放
     */
    fun togglePlay()
    /**
     * 是否正在播放
     * @return 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 调整进度
     */
    fun seekTo(time: Int)

    /**
     * 释放播放器
     */
    fun release()

    /**
     *
     */
    fun isRelease(): Boolean

    /**
     * 获取当前播放的位置，单位秒
     * @return  获取当前播放的位置
     */
    fun getCurrentDuration(): Int

    /**
     * 获取视频总时长，单位秒
     * @return  获取视频总时长
     */
    fun getDuration(): Int

    /**
     * 获取当前播放百分比(0, 100)
     */
    fun getCurrentPercentage(): Int

    /**
     * 获取缓冲百分比
     * @return 获取缓冲百分比
     */
    fun getBufferedPercentage(): Int

    /**
     * 已缓存的时长，单位秒
     */
    fun getBufferedDuration(): Int

    /**
     * 设置是否循环播放
     * @param isLooping 布尔值
     */
    fun setLooping(isLooping: Boolean)

    /**
     * 设置倍速
     */
    fun setMultiple(speed: Float)

    /**
     * 获取倍速
     */
    fun getMultiple(): Float

}