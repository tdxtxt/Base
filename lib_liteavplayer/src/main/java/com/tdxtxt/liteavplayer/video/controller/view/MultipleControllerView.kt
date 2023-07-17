package com.tdxtxt.liteavplayer.video.controller.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.adapter.MultipleAdapter
import com.tdxtxt.liteavplayer.video.inter.AbsPopupWindowController
import com.tdxtxt.liteavplayer.video.inter.ScreenChangeLisenter

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   :
 * </pre>
 */
class MultipleControllerView : AbsPopupWindowController(), ScreenChangeLisenter {
    private var mMultipleAdapter: MultipleAdapter? = null
    override fun getLayoutResId() = R.layout.liteavlib_view_control_multiple

    override fun onCreate(isFirstInit: Boolean): Boolean {
        if(isFirstInit){
            mMultipleAdapter = MultipleAdapter()
            mMultipleAdapter?.setItemClickListenter {
                getPlayerView()?.setMultiple(it)
                hide()
            }
            findViewById<RecyclerView>(R.id.multiple_list)?.adapter = mMultipleAdapter
        }
        mMultipleAdapter?.setData(getPlayerView()?.getMultipleList())
        return false
    }

    override fun getAnchorView(): View? {
        return getPlayerView()?.getBaicView()?.getMultipleTextView()
    }

    override fun attach(playerView: TXVideoPlayerView) {
        super.attach(playerView)
        playerView.addScreenChangeLisenter(this)
    }

    override fun detach() {
        getPlayerView()?.removeScreenChangeLisenter(this)
        super.detach()
    }

    override fun onChange(isFullScreen: Boolean) {
        hide()
    }
}