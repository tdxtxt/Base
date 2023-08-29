package com.tdxtxt.liteavplayer.video.controller.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.adapter.BitrateAdapter
import com.tdxtxt.liteavplayer.video.inter.AbsPopupWindowController
import com.tdxtxt.liteavplayer.video.inter.ScreenChangeLisenter
import com.tencent.live2.impl.V2TXLiveUtils

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/14
 *     desc   : 流畅度切换
 * </pre>
 */
class BitrateControllerView : AbsPopupWindowController(), ScreenChangeLisenter {
    private var mBitrateAdapter: BitrateAdapter? = null
    override fun getLayoutResId() = R.layout.liteavlib_view_control_bitrate

    override fun onCreate(isFirstInit: Boolean): Boolean {
        if(isFirstInit){
            mBitrateAdapter = BitrateAdapter()
            mBitrateAdapter?.setItemClickListenter { position, value ->
                getPlayerView()?.setBitrate(value)
                hide()
            }
            findViewById<RecyclerView>(R.id.bitrate_list)?.adapter = mBitrateAdapter
        }
        val data = getPlayerView()?.getSupportedBitrates()
        mBitrateAdapter?.setData(data)
        return data == null || data.isEmpty()
    }

    override fun getAnchorView(): View? {
        return getPlayerView()?.getBaicView()?.getBitrateTextView()
    }

    override fun attach(playerView: TXVideoPlayerView) {
        super.attach(playerView)
        playerView.addScreenChangeLisenter(this)
    }

    override fun detach() {
        getPlayerView()?.removeScreenChangeLisenter(this)
        super.detach()
    }

    override fun onScreenChange(isFullScreen: Boolean) {
        hide()
    }

}