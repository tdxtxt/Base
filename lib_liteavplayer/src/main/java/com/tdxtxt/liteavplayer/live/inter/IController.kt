package com.tdxtxt.liteavplayer.live.inter

import com.tdxtxt.liteavplayer.live.TXLivePlayerView

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   :
 * </pre>
 */
interface IController {
    fun attach(liveView: TXLivePlayerView)
    fun detach()
}

interface IBasicController : IController{
    fun bindSurface()
    fun unBindSurface()
}
