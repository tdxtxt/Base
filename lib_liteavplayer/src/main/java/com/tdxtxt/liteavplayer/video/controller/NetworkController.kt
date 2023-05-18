package com.tdxtxt.liteavplayer.video.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.IController

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   : 网络控制，监听移动网络情况，将事件抛出去外部处理
 * </pre>
 */
class NetworkController : IController, BroadcastReceiver() {
    private var context: Context? = null
    private var mPlayerView: TXVideoPlayerView? = null
    override fun attach(playerView: TXVideoPlayerView) {
        mPlayerView = playerView
        this.context = playerView.context.applicationContext

        //动态注册广播监听
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(this, intentFilter)
    }

    override fun detach() {
        context?.unregisterReceiver(this)
        mPlayerView = null
        context = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
            val status = LiteavPlayerUtils.getNetworkState(context)
            mPlayerView?.getVideoManager()?.sendNetworkEvent(status)
        }
    }

}