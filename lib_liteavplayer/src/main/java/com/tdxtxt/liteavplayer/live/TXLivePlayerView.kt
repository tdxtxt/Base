package com.tdxtxt.liteavplayer.live

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.controller.view.BasicControllerView
import com.tdxtxt.liteavplayer.live.inter.ILivePlayer
import com.tencent.live2.V2TXLivePlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   : 直播播放器
 * </pre>
 */
class TXLivePlayerView : FrameLayout, ILivePlayer {
    private var mLiveMgr: LiveMananger? = null
    private var mWidthRatio = -1
    private var mHeightRatio = -1
    private lateinit var mBaicView: BasicControllerView

    fun setLiveManager(manager: LiveMananger){
        mLiveMgr = manager

        getBaicView().bindSurface()
    }
    fun getLiveManager() = mLiveMgr

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        if(attrs != null){
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.TXLivePlayerView)
            mHeightRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txHeightRatio, -1)
            mWidthRatio = attributes.getInteger(R.styleable.TXVideoPlayerView_txWidthRatio, -1)
            attributes.recycle()
        }
        initView(context)
    }

    private fun initView(context: Context){
        mBaicView = BasicControllerView(context)
        mBaicView.attach(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(mWidthRatio > 0 && mHeightRatio > 0){
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width.toFloat() / mWidthRatio.toFloat() * mHeightRatio.toFloat())
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY))
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    fun getBaicView() = mBaicView

    override fun getPlayer(): V2TXLivePlayer? {
        return mLiveMgr?.getPlayer()
    }

    override fun setLiveSource(url: String?) {
        mLiveMgr?.setLiveSource(url)
    }

    override fun resume() {
        mLiveMgr?.resume()
    }

    override fun pause() {
        mLiveMgr?.pause()
    }

    override fun release() {
        mLiveMgr?.release()
    }

}