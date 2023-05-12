package com.tdxtxt.liteavplayer.weight.controller.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.weight.TXVideoPlayerView
import com.tdxtxt.liteavplayer.weight.inter.IMultipleController
import com.tdxtxt.video.player.adapter.MultipleAdapter
import kotlinx.android.synthetic.main.liteavlib_view_control_multiple.view.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   :
 * </pre>
 */
class MultipleControllerView : FrameLayout, IMultipleController {
    private var mPlayerView: TXVideoPlayerView? = null
    private var mMultipleAdapter: MultipleAdapter? = null

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_control_multiple, this, true)
        setBackgroundResource(android.R.color.black)
        alpha = 0.7f
        mMultipleAdapter = MultipleAdapter().apply {
            setItemClickListenter {
                hide()
                mPlayerView?.setMultiple(it)
            }
        }

        multiple_list.adapter = mMultipleAdapter

        setOnClickListener {
            hide()
        }
    }

    override fun show() {
        val parentView = parent
        if(!(parentView is ViewGroup)){
            this.mPlayerView?.getBaicView()?.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        mMultipleAdapter?.setMultiple(mPlayerView?.getMultiple()?: 1f)
    }

    override fun hide() {
        val parentView = parent
        if(parentView is ViewGroup){
            parentView.removeView(this)
        }
    }

    override fun attach(playerView: TXVideoPlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mPlayerView = null
    }
}