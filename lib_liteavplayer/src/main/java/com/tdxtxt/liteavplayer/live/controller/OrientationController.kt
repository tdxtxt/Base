package com.tdxtxt.liteavplayer.live.controller

import android.content.Context
import android.view.OrientationEventListener
import com.tdxtxt.liteavplayer.live.TXLivePlayerView
import com.tdxtxt.liteavplayer.live.inter.IController
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.controller.OrientationController


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/12
 *     desc   :
 * </pre>
 */
class OrientationController : IController {
    companion object{
        const val VERITCAL = 0
        const val HORIZONTA_REVERSE = 1
        const val HORIZONTA_FORWARD = 2
    }
    private var mPlayerView: TXLivePlayerView? = null
    private var mContext: Context? = null
    private var mOrientation = OrientationController.VERITCAL
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

    fun isVeritcal() = mOrientation == OrientationController.VERITCAL
    fun isReverseHorizontal() = mOrientation == OrientationController.HORIZONTA_REVERSE
    fun isForwardHorizontal() = mOrientation == OrientationController.HORIZONTA_FORWARD

    fun startWatch() {
        if (mOrientationListener == null)
            mOrientationListener = object : OrientationEventListener(mContext) {
                override fun onOrientationChanged(orientation: Int) {
                    if(orientation == ORIENTATION_UNKNOWN) return

                    if (isVeritcal(orientation)) {
                        if (mOrientation == OrientationController.VERITCAL) return
                        mOrientation = OrientationController.VERITCAL
//                        mPlayerView?.stopFullScreen()
                    } else if (isReverseHorizontal(orientation)) {
                        if (mOrientation == OrientationController.HORIZONTA_REVERSE) return
                        mOrientation = OrientationController.HORIZONTA_REVERSE
                        if(mPlayerView?.isFullScreen() == true){
                            mPlayerView?.startFullScreen(true)
                        }
                    } else if (isForwardHorizontal(orientation)) {
                        if (mOrientation == OrientationController.HORIZONTA_FORWARD) return
                        mOrientation = OrientationController.HORIZONTA_FORWARD
                        if(mPlayerView?.isFullScreen() == true){
                            mPlayerView?.startFullScreen(false)
                        }
                    }
                }
            }
        mOrientationListener?.enable()
    }

    override fun attach(playerView: TXLivePlayerView) {
        this.mPlayerView = playerView
    }

    override fun detach() {
        mOrientationListener?.disable()
        mPlayerView = null
    }
}