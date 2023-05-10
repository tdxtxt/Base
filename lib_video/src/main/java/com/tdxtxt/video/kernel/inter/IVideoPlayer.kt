package com.tdxtxt.video.kernel.inter

import android.content.res.AssetFileDescriptor

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
interface IVideoPlayer {
    /*----------------------------第一部分：视频初始化实例对象方法----------------------------------*/
    /**
     * 设置播放地址
     * 视频播放器第二步：设置数据
     * @param path    播放地址
     */
    fun setDataSource(path: String?)

    /**
     * 用于播放raw和asset里面的视频文件
     */
    fun setDataSource(fd: AssetFileDescriptor?)

    /*----------------------------第二部分：视频播放器状态方法----------------------------------*/
    /**
     * 准备播放
     */
    fun prepare()
    /**
     * 播放，进度从当前播放停留的进度开始
     */
    fun start()
    /**
     * 重新播放，进度从0开始
     */
    fun reStart()

    /**
     * 暂停
     */
    fun pause()

    /**
     * 暂停/播放
     */
    fun togglePlay()

    /**
     * 停止
     */
    fun stop()

    /**
     * 是否正在播放
     * @return 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 调整进度
     */
    fun seekTo(time: Long)

    /**
     * 精准调整进度
     */
    fun accurateSeekTo(time: Long)

    /**
     * 释放播放器
     */
    fun release()


    /**
     * 是否释放
     */
    fun isRelease(): Boolean

    /**
     * 获取当前播放的位置
     * @return  获取当前播放的位置
     */
    fun getCurrentDuration(): Long

    /**
     * 获取视频总时长
     * @return  获取视频总时长
     */
    fun getDuration(): Long

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
     * 已缓存的时长
     */
    fun getBufferedDuration(): Long


    /**
     * 设置音量
     */
    fun setVolume(volume: Float)

    /**
     * 获取音量
     */
    fun getVolume(): Float

    /**
     * 设置是否循环播放
     * @param isLooping 布尔值
     */
    fun setLooping(isLooping: Boolean)

    /**
     * 设置播放速度
     * @param speed 速度
     */
    fun setSpeed(speed: Float)

    /**
     * 获取播放速度
     * @return 播放速度
     */
    fun getSpeed(): Float

    /**
     * 获取当前缓冲的网速
     * @return  获取网络
     */
    fun getTcpSpeed(): Long
}