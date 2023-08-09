package com.tdxtxt.liteavplayer.video.controller

import android.content.Context
import android.view.OrientationEventListener
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.inter.IController

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/1
 *     desc   : 设备方向监听
 * </pre>
 */
class OrientationController: IController {
    companion object{
        const val VERITCAL = 0
        const val HORIZONTA_REVERSE = 1
        const val HORIZONTA_FORWARD = 2
    }
    private var mPlayerView: TXVideoPlayerView? = null
    private var mOrientation = VERITCAL
    private var mOrientationListener: OrientationEventListener? = null

    private fun isVeritcal(orientation: Int): Boolean { //0度 或 180度
        return (orientation > 350 || orientation < 10) || (orientation > 170 && orientation < 190)
    }

    private fun isReverseHorizontal(orientation: Int): Boolean { //90度
        return orientation > 80 && orientation < 100
    }

    private fun isForwardHorizontal(orientation: Int): Boolean {  //270度
        return orientation > 260 && orientation < 280
    }

    fun isVeritcal() = mOrientation == VERITCAL
    fun isReverseHorizontal() = mOrientation == HORIZONTA_REVERSE
    fun isForwardHorizontal() = mOrientation == HORIZONTA_FORWARD

    fun startWatch() {
        val context = mPlayerView?.context ?: return

        if (mOrientationListener == null)
            mOrientationListener = object : OrientationEventListener(context) {
                override fun onOrientationChanged(orientation: Int) {
                    if(orientation == ORIENTATION_UNKNOWN) return

                    if (isVeritcal(orientation)) {
                        if (mOrientation == VERITCAL) return
                        mOrientation = VERITCAL
//                        mPlayerView?.stopFullScreen()
                    } else if (isReverseHorizontal(orientation)) {
                        if (mOrientation == HORIZONTA_REVERSE) return
                        mOrientation = HORIZONTA_REVERSE
//                        if(mPlayerView?.isFullScreen() == true){
//                            mPlayerView?.startFullScreen(true)
//                        }
                    } else if (isForwardHorizontal(orientation)) {
                        if (mOrientation == HORIZONTA_FORWARD) return
                        mOrientation = HORIZONTA_FORWARD
//                        if(mPlayerView?.isFullScreen() == true){
//                            mPlayerView?.startFullScreen(false)
//                        }
                    }
                }
            }
        mOrientationListener?.enable()
    }

    fun stopWatch(){
        mOrientationListener?.disable()
        mOrientationListener = null
    }

    override fun attach(playerView: TXVideoPlayerView) {
        this.mPlayerView = playerView
        startWatch()
    }

    override fun detach() {
        stopWatch()
        mPlayerView = null
    }

}