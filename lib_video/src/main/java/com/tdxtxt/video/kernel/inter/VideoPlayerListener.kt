package com.tdxtxt.video.kernel.inter

import com.tdxtxt.video.utils.PlayerConstant


/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 播放器event监听
 * </pre>
 */
interface VideoPlayerListener {
    /**
     * 播放状态，主要是指播放器的各种状态
     * PlayerConstant.PlaylerState.STATE_ERROR               播放错误
     * PlayerConstant.PlaylerState.STATE_STOP                播放初始状态，停止状态
     * PlayerConstant.PlaylerState.STATE_PREPARED            准备成功
     * PlayerConstant.PlaylerState.STATE_START               播放开始
     * PlayerConstant.PlaylerState.STATE_PLAYING             正在播放
     * PlayerConstant.PlaylerState.STATE_PAUSED              暂停播放
     * PlayerConstant.PlaylerState.STATE_COMPLETED           播放完成
     * PlayerConstant.PlaylerState.STATE_BUFFERING           正在缓冲
     * PlayerConstant.PlaylerState.STATE_RELEASE             释放播放器内核
     *
     * PlayerConstant.PlaylerState.STATE_ERROR               播放内容尺寸
     */
    fun onPlayStateChanged(@PlayerConstant.PlaylerState state: Int, value: Any? = null)
}