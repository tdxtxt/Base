package com.tdxtxt.liteavplayer.live.controller.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IBasicController
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.*
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.basic_back
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.basic_menu
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.basic_surface
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.basic_toggle_orient
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_live.view.basic_toggleplay_small
import kotlinx.android.synthetic.main.liteavlib_view_basic_controller_video.view.*
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   :
 * </pre>
 */
class BasicControllerView : FrameLayout, IBasicController {
    private var mPlayerView: TXLivePlayerView? = null
    private val mDefaultFadeTimeout = 7000L
    private var mLastBasicMenuLayoutShowTime = 0L
    private val mFadeBasicMenuLayoutRunnable = Runnable {
        basic_menu.visibility = View.GONE
    }

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }
    private fun initView(context: Context){
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        LayoutInflater.from(context).inflate(R.layout.liteavlib_view_basic_controller_live, this, true)

        clickView(basic_back)
        clickView(basic_toggle_orient)

        showBasicMenuLayout()
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            when(it){
                basic_back -> mPlayerView?.back()
                basic_toggle_orient -> {
                    if(mPlayerView?.isFullScreen() == true){
                        mPlayerView?.stopFullScreen()
                    }else{
                        mPlayerView?.startFullScreen()
                    }
                }
            }
        }
    }

    override fun bindSurface() {
        mPlayerView?.getPlayer()?.setRenderView(basic_surface)
    }

    override fun unBindSurface() {

    }

    override fun getViewWidth(): Int {
        return width
    }

    override fun getViewHeight(): Int {
        return height
    }

    override fun showBasicMenuLayout() {
        if(abs(System.currentTimeMillis() - mLastBasicMenuLayoutShowTime) > 800){
            mLastBasicMenuLayoutShowTime = System.currentTimeMillis()
            removeCallbacks(mFadeBasicMenuLayoutRunnable)
            basic_menu.visibility = View.VISIBLE
            postDelayed(mFadeBasicMenuLayoutRunnable, mDefaultFadeTimeout)
        }
    }

    override fun hideBasicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        mFadeBasicMenuLayoutRunnable.run()
    }

    override fun toggleBaicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        if(basic_menu.visibility == View.VISIBLE){
            hideBasicMenuLayout()
        }else{
            showBasicMenuLayout()
        }
    }

    override fun updateFullScreen(isFullScreen: Boolean?) {
        if(isFullScreen == true){
            basic_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_small)
        }else{
            basic_toggle_orient.setImageResource(R.mipmap.liteavlib_ic_orient_large)
        }
    }

    override fun attach(playerView: TXLivePlayerView) {
        this.mPlayerView = playerView
        playerView.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun detach() {
        basic_surface?.onDestroy()
    }
}