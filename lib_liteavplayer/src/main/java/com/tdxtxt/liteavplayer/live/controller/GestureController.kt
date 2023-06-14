package com.tdxtxt.liteavplayer.live.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IController
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import kotlin.math.abs

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/27
 *     desc   :
 * </pre>
 */
class GestureController : IController {
    private var mContext: Context? = null
    private var mPlayerView: TXLivePlayerView? = null
    private var isHorizenalDistance = false
    private var isLeftVerticalDistance = false
    private var isRightVerticalDistance = false

    private val mGestureDetector = GestureDetector(mContext, object : GestureDetector.SimpleOnGestureListener(){
        var xDown: Float = 0f
        var yDown: Float = 0f
        var currentDuration = 0

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mPlayerView?.getBaicView()?.toggleBaicMenuLayout()
            return false
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?,
            distanceX: Float, distanceY: Float): Boolean {

            if(isStatusBar(yDown)){
                return true
            }

            if(abs(distanceX) >= abs(distanceY)){//水平滑动
                if(isLeftVerticalDistance || isRightVerticalDistance){

                }else{
                    onHorizontalDistance(currentDuration, e1?.x ?: 0f, e2?.x ?: 0f)
                }
            }else{//垂直滑动
                if(isHorizenalDistance){

                }else{
                    if(isLeft(xDown)){
                        onLeftVerticalDistance(e1?.y ?: 0f, e2?.y ?: 0f)
                    }else if(isRight(xDown)){
                        onRightVerticalDistance(e1?.y ?: 0f, e2?.y ?: 0f)
                    }
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {//长按事件

        }

        override fun onDown(e: MotionEvent?): Boolean {
            xDown = e?.x ?: 0f
            yDown = e?.y ?: 0f
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    })

    fun onHorizontalDistance(currentDuration: Int, donwX: Float, nowX: Float){
        isHorizenalDistance = true
    }

    fun onLeftVerticalDistance(donwY: Float, nowY: Float){
        isLeftVerticalDistance = true

        mPlayerView?.getBaicView()?.hideBasicMenuLayout()

        val deltaX = donwY - nowY
        val height = mPlayerView?.getBaicView()?.getViewWidth()?: 1
        val changePercent = deltaX * 2 / height
        mPlayerView?.getBrightControllerView()?.show(changePercent)
    }

    fun onRightVerticalDistance(donwY: Float, nowY: Float){
        isRightVerticalDistance = true

        mPlayerView?.getBaicView()?.hideBasicMenuLayout()

        val deltaX = donwY - nowY
        val height = mPlayerView?.getBaicView()?.getViewWidth()?: 1
        val changePercent = deltaX * 2 / height
        mPlayerView?.getVolumeControllerView()?.show(changePercent)
    }

    fun onGestureEnd(){
        if(isRightVerticalDistance){
            mPlayerView?.getVolumeControllerView()?.hide()
        }
        if(isLeftVerticalDistance){
            mPlayerView?.getBrightControllerView()?.hide()
        }
        if(isHorizenalDistance){

        }
    }

    private fun isLeft(x: Number): Boolean{
        return x.toFloat() < (mPlayerView?.getBaicView()?.getViewWidth()?: 1) / 2f
    }

    private fun isRight(x: Number): Boolean{
        return x.toFloat() > (mPlayerView?.getBaicView()?.getViewWidth()?: 1) / 2f
    }

    private fun isStatusBar(y: Number): Boolean {
        return y.toInt() < LiteavPlayerUtils.getStatusBarHeight(mPlayerView?.context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun attach(playerView: TXLivePlayerView) {
        this.mPlayerView = playerView
        this.mPlayerView?.getBaicView()?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    onGestureEnd()
                    isHorizenalDistance = false
                    isRightVerticalDistance = false
                    isLeftVerticalDistance = false
                }
            }
            mGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    override fun detach() {
        mPlayerView = null
    }

}