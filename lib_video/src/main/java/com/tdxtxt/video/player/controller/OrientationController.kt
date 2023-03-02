package com.tdxtxt.video.player.controller

import android.content.Context
import android.view.OrientationEventListener
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.utils.PlayerConstant

/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/1
 *     desc   : 设备方向监听
 * </pre>
 */
class OrientationController: IController {
    private var mContainer: VideoPlayerView? = null
    private var mContext: Context? = null
    private var mOrientation = PlayerConstant.VERITCAL
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

    fun isVeritcal() = mOrientation == PlayerConstant.VERITCAL
    fun isReverseHorizontal() = mOrientation == PlayerConstant.HORIZONTA_REVERSE
    fun isForwardHorizontal() = mOrientation == PlayerConstant.HORIZONTA_FORWARD

    fun startWatch() {
        if (mOrientationListener == null)
            mOrientationListener = object : OrientationEventListener(mContext) {
                override fun onOrientationChanged(orientation: Int) {
                    if(orientation == ORIENTATION_UNKNOWN) return

                    if (isVeritcal(orientation)) {
                        if (mOrientation == PlayerConstant.VERITCAL) return
                        mOrientation =
                            PlayerConstant.VERITCAL
//                        mContainer?.stopFullScreen()
                    } else if (isReverseHorizontal(orientation)) {
                        if (mOrientation == PlayerConstant.HORIZONTA_REVERSE) return
                        mOrientation =
                            PlayerConstant.HORIZONTA_REVERSE
                        if(mContainer?.isFullScreen() == true){
                            mContainer?.startFullScreen(true)
                        }
                    } else if (isForwardHorizontal(orientation)) {
                        if (mOrientation == PlayerConstant.HORIZONTA_FORWARD) return
                        mOrientation =
                            PlayerConstant.HORIZONTA_FORWARD
                        if(mContainer?.isFullScreen() == true){
                            mContainer?.startFullScreen(false)
                        }
                    }
                }
            }
        mOrientationListener?.enable()
    }

    override fun attach(container: VideoPlayerView) {
        this.mContainer = container
        this.mContext = container.context.applicationContext

        startWatch()
    }

    override fun detach() {
        mOrientationListener?.disable()
    }
}