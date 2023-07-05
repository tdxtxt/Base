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
            var EVENT_ERROR = -1    //播放错误
            var EVENT_PREPARED = 1  //准备成功
            var EVENT_START = 2     //播放开始
            var EVENT_PLAYING = 3   //播放中
            var EVENT_PAUSED = 4    //播放暂停
            var EVENT_COMPLETED = 5 //播放完成
//            var EVENT_STOP = 6      //播放停止
            var EVENT_RELEASE = 7   //释放
            var EVENT_LOADING = 8   //缓冲开始、缓冲结束

            var CHANGE_NETSPEED = 20  //网速变化
            var CHANGE_NETWORK = 21  //网络变化
            var CHANGE_MULTIPLE = 22 //播放速度变化

            var EVENT_NONDRAG = 30 // 不可拖拽

            var EVENT_UNKOWN = 100 //未知事件
        }
    }
    /**
     * 播放状态，主要是指播放器的各种状态
     * PlaylerState.EVENT_ERROR               播放错误
     * PlaylerState.EVENT_STOP                播放初始状态，停止状态
     * PlaylerState.EVENT_PREPARED            准备成功
     * PlaylerState.EVENT_START               播放开始
     * PlaylerState.STATE_PLAYING             正在播放
     * PlaylerState.EVENT_PAUSED              暂停播放
     * PlaylerState.EVENT_COMPLETED           播放完成
     * PlaylerState.EVENT_LOADING             正在缓冲
     * PlaylerState.EVENT_RELEASE             释放播放器内核
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
