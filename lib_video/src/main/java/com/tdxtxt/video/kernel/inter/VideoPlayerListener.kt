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
    fun onPlayStateChanged(@PlayerConstant.PlaylerState state: Int, value: Any? = null)
}