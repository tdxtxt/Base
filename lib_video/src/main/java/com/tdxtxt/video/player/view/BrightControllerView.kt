package com.tdxtxt.video.player.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.tdxtxt.video.R
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.IControllerGesture

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   : 亮度调节
 * </pre>
 */
class BrightControllerView: FrameLayout, IControllerGesture {
    private var mContainer: VideoPlayerView? = null
    private var mBrightness: Float = 0f

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView() }

    private fun initView(){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.libvideo_view_control_volume_bright, this, true)
        findViewById<ImageView>(R.id.iv_volume_bright)?.setImageResource(R.mipmap.libvideo_ic_bright)
    }

    override fun show(changePercent: Float) {
        val parentView = parent
        if(!(parentView is ViewGroup)){
            this.mContainer?.getControlWrapper()?.addView(this)
        }

        val activity = context
        if(activity is Activity){
            activity.window.apply {
                if(mBrightness == 0f) mBrightness = if(attributes.screenBrightness == BRIGHTNESS_OVERRIDE_NONE) 0.5f else attributes.screenBrightness
                var newBrightness = mBrightness + mBrightness * changePercent
                if(newBrightness < 0.1f) newBrightness = 0.1f
                if(newBrightness > 1f) newBrightness = 1f

                val lp = attributes
                lp.screenBrightness = newBrightness
                attributes = lp
                findViewById<ProgressBar>(R.id.progress_volume_bright)?.progress = (newBrightness * 100).toInt()
            }
        }
    }

    override fun hide() {
        mBrightness = 0f
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