package com.tdxtxt.liteavplayer.video.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.IController
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
    private var mPlayerView: TXVideoPlayerView? = null
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
            mPlayerView?.togglePlay()
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
            currentDuration = mPlayerView?.getCurrentDuration()?: 0
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    })

    fun onHorizontalDistance(currentDuration: Int, donwX: Float, nowX: Float){
        isHorizenalDistance = true

        val deltaX = nowX - donwX
        if(abs(deltaX) < 5) return
        val totalTime = mPlayerView?.getDuration()?: 0
        if(totalTime <= 0) return
        if(mPlayerView?.getBaicView() == null) return
        val viewWidth = mPlayerView?.getBaicView()?.getViewWidth()?: 1

        val deltaPosition = deltaX / viewWidth * totalTime
        var targetTime: Int = currentDuration +
                (if (totalTime >= 60 * 60 * 1000) {
                    (deltaPosition / 8f)
                } else if (totalTime > 30 * 60 * 1000) {
                    (deltaPosition / 4f)
                } else if (totalTime > 10 * 60 * 1000) {
                    (deltaPosition / 3f).toLong()
                } else if (totalTime > 3 * 60 * 1000) {
                    (deltaPosition / 2f)
                } else deltaPosition).toInt()
        if(targetTime < 0) targetTime = 0
        if(targetTime > totalTime) targetTime = totalTime

        val progress = (targetTime.toFloat() / totalTime.toFloat() * 100).toInt()
        mPlayerView?.getBaicView()?.apply {
            setTrackingSeekBar(true)
            updateSeekBar(progress)
            showBasicMenuLayout()
        }
    }

    fun onLeftVerticalDistance(donwY: Float, nowY: Float){
        isLeftVerticalDistance = true

        mPlayerView?.getBaicView()?.hideBasicMenuLayout()

        val deltaX = donwY - nowY
        val height = mPlayerView?.getBaicView()?.getViewHeight()?: 1
        val changePercent = deltaX * 2f / height.toFloat()
        mPlayerView?.getBrightControllerView()?.show(changePercent)
    }

    fun onRightVerticalDistance(donwY: Float, nowY: Float){
        isRightVerticalDistance = true

        mPlayerView?.getBaicView()?.hideBasicMenuLayout()

        val deltaX = donwY - nowY
        val height = mPlayerView?.getBaicView()?.getViewHeight()?: 1
        val changePercent = deltaX * 2f / height.toFloat()
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
            mPlayerView?.getBaicView()?.setTrackingSeekBar(false)
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
    override fun attach(playerView: TXVideoPlayerView) {
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