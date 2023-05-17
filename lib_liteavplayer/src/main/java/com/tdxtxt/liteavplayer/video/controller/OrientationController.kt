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
    private var mContext: Context? = null
    private var mOrientation = VERITCAL
    private var mOrientationListener: OrientationEventListener? = null

    private fun isVeritcal(orientation: Int): Boolean {
        return (orientation > 350 || orientation < 10) || (orientation > 170 && orientation < 190)
    }

    private fun isReverseHorizontal(orientation: Int): Boolean {
        return orientation > 80 && orientation < 100
    }

    private fun isForwardHorizontal(orientation: Int): Boolean {
        return orientation > 260 && orientation < 280
    }

    fun isVeritcal() = mOrientation == VERITCAL
    fun isReverseHorizontal() = mOrientation == HORIZONTA_REVERSE
    fun isForwardHorizontal() = mOrientation == HORIZONTA_FORWARD

    fun startWatch() {
        if (mOrientationListener == null)
            mOrientationListener = object : OrientationEventListener(mContext) {
                override fun onOrientationChanged(orientation: Int) {
                    if(orientation == ORIENTATION_UNKNOWN) return

                    if (isVeritcal(orientation)) {
                        if (mOrientation == VERITCAL) return
                        mOrientation = VERITCAL
//                        mPlayerView?.stopFullScreen()
                    } else if (isReverseHorizontal(orientation)) {
                        if (mOrientation == HORIZONTA_REVERSE) return
                        mOrientation = HORIZONTA_REVERSE
                        if(mPlayerView?.isFullScreen() == true){
                            mPlayerView?.startFullScreen(true)
                        }
                    } else if (isForwardHorizontal(orientation)) {
                        if (mOrientation == HORIZONTA_FORWARD) return
                        mOrientation = HORIZONTA_FORWARD
                        if(mPlayerView?.isFullScreen() == true){
                            mPlayerView?.startFullScreen(false)
                        }
                    }
                }
            }
        mOrientationListener?.enable()
    }

    override fun attach(playerView: TXVideoPlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mOrientationListener?.disable()
        mPlayerView = null
    }

}