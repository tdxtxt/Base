package com.tdxtxt.liteavplayer.video.controller.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.IMultipleController
import com.tdxtxt.liteavplayer.video.adapter.MultipleAdapter
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
        setBackgroundResource(R.drawable.liteavlib_background_multiple)

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
            this.mPlayerView?.getBaicView()?.getMultiplePlaceHolder()?.addView(this, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        }
        mMultipleAdapter?.setData(mPlayerView?.getMultipleList())
    }

    override fun hide() {
        val parentView = parent
        if(parentView is ViewGroup){
            parentView.removeView(this)
        }
    }

    override fun toggle() {
        if(parent == null){
            show()
        }else{
            hide()
        }
    }

    override fun attach(playerView: TXVideoPlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mPlayerView = null
    }
}