package com.tdxtxt.liteavplayer.live.controller.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IBasicController
import com.tencent.rtmp.ui.TXCloudVideoView
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   :
 * </pre>
 */
class BasicControllerView : FrameLayout, IBasicController {
    private var basic_menu: FrameLayout? = null
    private var basic_back: View? = null
    private var basic_toggle_orient: ImageView? = null
    private var basic_surface: TXCloudVideoView? = null


    private var mPlayerView: TXLivePlayerView? = null
    private val mDefaultFadeTimeout = 7000L
    private var mLastBasicMenuLayoutShowTime = 0L
    private val mFadeBasicMenuLayoutRunnable = Runnable {
        basic_menu?.visibility = View.GONE
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
        findByIdView()

        clickView(basic_back)
        clickView(basic_toggle_orient)

        showBasicMenuLayout()
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            when(it.id){
                R.id.basic_back -> mPlayerView?.back()
                R.id.basic_toggle_orient -> {
                    if(mPlayerView?.isFullScreen() == true){
                        mPlayerView?.stopFullScreen()
                    }else{
                        mPlayerView?.startFullScreen()
                    }
                }
            }
        }
    }

    private fun findByIdView(){
        basic_menu = findViewById(R.id.basic_menu)
        basic_back = findViewById(R.id.basic_back)
        basic_toggle_orient = findViewById(R.id.basic_toggle_orient)
        basic_surface = findViewById(R.id.basic_surface)
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
            basic_menu?.visibility = View.VISIBLE
            postDelayed(mFadeBasicMenuLayoutRunnable, mDefaultFadeTimeout)
        }
    }

    override fun hideBasicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        mFadeBasicMenuLayoutRunnable.run()
    }

    override fun toggleBaicMenuLayout() {
        removeCallbacks(mFadeBasicMenuLayoutRunnable)
        if(basic_menu?.visibility == View.VISIBLE){
            hideBasicMenuLayout()
        }else{
            showBasicMenuLayout()
        }
    }

    override fun updateFullScreen(isFullScreen: Boolean?) {
        if(isFullScreen == true){
            basic_toggle_orient?.setImageResource(R.mipmap.liteavlib_ic_orient_small)
        }else{
            basic_toggle_orient?.setImageResource(R.mipmap.liteavlib_ic_orient_large)
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