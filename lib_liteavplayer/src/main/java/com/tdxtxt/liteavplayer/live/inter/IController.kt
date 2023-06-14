package com.tdxtxt.liteavplayer.live.inter

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import com.tdxtxt.liteavplayer.live.TXLivePlayerView

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   :
 * </pre>
 */
interface IController {
    fun attach(playerView: TXLivePlayerView)
    fun detach()
}

interface ILiveView {
    /**
     * 显示自定义view
     */
    fun showCustomView(iView: AbsCustomController)

    /**
     * 隐藏自定义view
     */
    fun hideCustomView()
    /**
     * 是否可返回
     */
    fun onBackPressed(): Boolean

    /**
     * 返回操作处理
     */
    fun back()

    /**
     * 是否全屏播放
     */
    fun isFullScreen(): Boolean

    /**
     * 停止全屏播放
     */
    fun stopFullScreen()

    /**
     * 开始全屏播放
     */
    fun startFullScreen(isReverse: Boolean? = null)
}

interface IBasicController : IController{
    fun bindSurface()
    fun unBindSurface()
    fun getViewWidth(): Int
    fun getViewHeight(): Int
    fun showBasicMenuLayout()
    fun hideBasicMenuLayout()
    fun toggleBaicMenuLayout()

    fun updateFullScreen(isFullScreen: Boolean?)
}

interface IGestureController : IController {
    fun show(changePercent: Float)
    fun hide()
}


abstract class AbsCustomController: IController {
    private var mView: View? = null
    private var mPlayerView: TXLivePlayerView? = null
    fun getPlayerView() = mPlayerView
    abstract fun getLayoutResId(): Int
    abstract fun onCreate()

    override fun attach(playerView: TXLivePlayerView) {
        mPlayerView = playerView
        onCreate()
    }

    override fun detach() {
        mPlayerView = null
        mView = null
    }

    fun inflater(playerView: TXLivePlayerView): View? {
        mView = LayoutInflater.from(playerView.context).inflate(getLayoutResId(), null, false)
        //禁止手势传递
        mView?.setOnTouchListener { v, event ->  true}
        mView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
            override fun onViewAttachedToWindow(v: View?) {}
            override fun onViewDetachedFromWindow(v: View?) {
                mView?.removeOnAttachStateChangeListener(this)
                detach()
            }
        })
        attach(playerView)//必须放到最后
        return mView
    }

    fun <T : View> findViewById(@IdRes id: Int): T? {
        return mView?.findViewById(id)
    }

}
