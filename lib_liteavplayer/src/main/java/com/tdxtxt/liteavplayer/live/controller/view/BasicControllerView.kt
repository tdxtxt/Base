package com.tdxtxt.liteavplayer.live.controller.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IBasicController
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   :
 * </pre>
 */
class BasicControllerView : FrameLayout, IBasicController {
    private var mPlayerView: TXLivePlayerView? = null
    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }
    private fun initView(context: Context){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_basic_controller_live, this, true)
    }

    override fun bindSurface() {
        mPlayerView?.getPlayer()?.setRenderView(basic_surface)
    }

    override fun unBindSurface() {

    }

    override fun attach(liveView: TXLivePlayerView) {
        this.mPlayerView = liveView
        liveView.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun detach() {
        basic_surface?.onDestroy()
    }
}