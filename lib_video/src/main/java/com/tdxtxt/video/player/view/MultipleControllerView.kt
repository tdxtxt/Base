package com.tdxtxt.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tdxtxt.video.R
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.adapter.MultipleAdapter
import com.tdxtxt.video.player.controller.IControllerMultiple
import kotlinx.android.synthetic.main.libvideo_view_control_multiple.view.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   :
 * </pre>
 */
class MultipleControllerView : FrameLayout, IControllerMultiple {
    private var mContainer: VideoPlayerView? = null
    private var mMultipleAdapter: MultipleAdapter? = null

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_control_multiple, this, true)
        setBackgroundResource(android.R.color.black)
        alpha = 0.7f
        mMultipleAdapter = MultipleAdapter().apply {
            setItemClickListenter {
                hide()
                mContainer?.setSpeed(it)
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
            this.mContainer?.getControlWrapper()?.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        mMultipleAdapter?.setMultiple(mContainer?.getSpeed()?: 1f)
    }

    override fun hide() {
        val parentView = parent
        if(parentView is ViewGroup){
            parentView.removeView(this)
        }
    }

    override fun attach(container: VideoPlayerView) {
        this.mContainer = container
    }

    override fun detach() {

    }
}