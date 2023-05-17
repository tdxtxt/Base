package com.tdxtxt.liteavplayer.video.inter


/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 播放器event监听
 * </pre>
 */
interface TXPlayerListener {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class PlayerState {
        companion object {
            var STATE_ERROR = -1    //播放错误
            var STATE_PREPARED = 1  //准备成功
            var STATE_START = 2     //播放开始
            var STATE_PLAYING = 3   //播放中
            var STATE_PAUSED = 4    //播放暂停
            var STATE_COMPLETED = 5 //播放完成
            var STATE_RELEASE = 7   //释放
            var STATE_LOADING = 8   //缓冲中...

            var CHANGE_NETSPEED = 20  //网速变化
            var CHANGE_NETWORK = 21  //网络变化
            var CHANGE_MULTIPLE = 22 //播放速度变化
        }
    }
    /**
     * 播放状态，主要是指播放器的各种状态
     * PlaylerState.STATE_ERROR               播放错误
     * PlaylerState.STATE_STOP                播放初始状态，停止状态
     * PlaylerState.STATE_PREPARED            准备成功
     * PlaylerState.STATE_START               播放开始
     * PlaylerState.STATE_PLAYING             正在播放
     * PlaylerState.STATE_PAUSED              暂停播放
     * PlaylerState.STATE_COMPLETED           播放完成
     * PlaylerState.STATE_BUFFERING           正在缓冲
     * PlaylerState.STATE_RELEASE             释放播放器内核
     *
     * PlaylerState.STATE_ERROR               播放内容尺寸
     */
    fun onPlayStateChanged(@PlayerState state: Int, value: Any? = null)
}
/**
 * 播放状态，主要是指播放器的各种状态
 * -1               播放错误
 * 0                播放初始状态，停止状态
 * 1                准备成功
 * 2                播放开始
 * 3                正在播放
 * 4                暂停播放
 * 5                播放完成
 * 6                正在缓冲
 * 7                释放播放器内核
 *
 * 11               播放内容尺寸
 */
