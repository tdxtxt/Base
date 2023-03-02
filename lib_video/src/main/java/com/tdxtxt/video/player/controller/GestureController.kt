package com.tdxtxt.video.player.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.tdxtxt.video.player.VideoPlayerView
import kotlin.math.abs

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/27
 *     desc   :
 * </pre>
 */
class GestureController() : IController {
    private var mContext: Context? = null
    private var mContainer: VideoPlayerView? = null
//    private var mVideoPlayer: AbstractVideoPlayer? = null
//    private var mWrapperController: ControlWrapperView? = null
    private var isHorizenalDistance = false
    private var isLeftVerticalDistance = false
    private var isRightVerticalDistance = false

    private val mGestureDetector = GestureDetector(mContext, object : GestureDetector.SimpleOnGestureListener(){
        var xDown: Float = 0f
        var currentDuration = 0L

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mContainer?.getControlWrapper()?.toggleMenu()
            return false
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mContainer?.getVideoPlayer()?.togglePlay()
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?,
            distanceX: Float, distanceY: Float): Boolean {
            if(abs(distanceX) >= abs(distanceY)){//水平滑动
                if(isLeftVerticalDistance || isRightVerticalDistance){

                }else{
                    isHorizenalDistance = true
                    onHorizontalDistance(currentDuration, e1?.x ?: 0f, e2?.x ?: 0f)
                }
            }else{//垂直滑动
                if(isHorizenalDistance){

                }else{
                    if(isLeft(xDown)){
                        isLeftVerticalDistance = true
                        onLeftVerticalDistance(e1?.y ?: 0f, e2?.y ?: 0f)
                    }else if(isRight(xDown)){
                        isRightVerticalDistance = true
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
            currentDuration = mContainer?.getCurrentDuration()?: 0L
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    })

    fun onHorizontalDistance(currentDuration: Long, donwX: Float, nowX: Float){
        val deltaX = nowX - donwX
        if(abs(deltaX) < 5) return
        val totalTime = mContainer?.getDuration()?: 0L
        if(totalTime <= 0) return
        if(mContainer?.getControlWrapper() == null) return
        val viewWidth = mContainer?.getControlWrapper()?.getViewWidth()?: 1

        val deltaPosition = deltaX / viewWidth * totalTime
        var targetTime: Long = currentDuration +
                (if (totalTime >= 60 * 60 * 1000) {
                    (deltaPosition / 8f).toLong()
                } else if (totalTime > 30 * 60 * 1000) {
                    (deltaPosition / 4f).toLong()
                } else if (totalTime > 10 * 60 * 1000) {
                    (deltaPosition / 3f).toLong()
                } else if (totalTime > 3 * 60 * 1000) {
                    (deltaPosition / 2f).toLong()
                } else deltaPosition.toLong())
        if(targetTime < 0) targetTime = 0
        if(targetTime > totalTime) targetTime = totalTime

        mContainer?.getControlWrapper()?.scrollSeekBar(targetTime)
        }

    fun onLeftVerticalDistance(donwY: Float, nowY: Float){
        mContainer?.getControlWrapper()?.hideMenu()

        val deltaX = donwY - nowY
        val height = mContainer?.getControlWrapper()?.getViewWidth()?: 1
        val changePercent = deltaX * 2 / height
        mContainer?.getBrightController()?.show(changePercent)
    }

    fun onRightVerticalDistance(donwY: Float, nowY: Float){
        mContainer?.getControlWrapper()?.hideMenu()

        val deltaX = donwY - nowY
        val height = mContainer?.getControlWrapper()?.getViewWidth()?: 1
        val changePercent = deltaX * 2 / height
        mContainer?.getVolumeController()?.show(changePercent)
    }

    fun onGestureEnd(){
        mContainer?.getVolumeController()?.hide()
        mContainer?.getBrightController()?.hide()
    }

    private fun isLeft(x: Number): Boolean{
        return x.toFloat() < (mContainer?.getControlWrapper()?.getViewWidth()?: 1) / 2f
    }

    private fun isRight(x: Number): Boolean{
        return x.toFloat() > (mContainer?.getControlWrapper()?.getViewWidth()?: 1) / 2f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun attach(container: VideoPlayerView) {
        this.mContainer = container

        this.mContainer?.getControlWrapper()?.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL ->{
                    isHorizenalDistance = false
                    isRightVerticalDistance = false
                    isLeftVerticalDistance = false
                    onGestureEnd()
                }
            }
            mGestureDetector.onTouchEvent(event)

            return@setOnTouchListener true
        }
    }

    override fun detach() {

    }

}