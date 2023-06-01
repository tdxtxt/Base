package com.tdxtxt.liteavplayer.video.inter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tencent.rtmp.TXVodPlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
interface IController {
    /**
     * 捆绑父容器
     */
    fun attach(playerView: TXVideoPlayerView)
    /**
     * 跟随activity/fragment生命周期，前提是必须调用{@link TXVideoPlayerView.bindLifecycle}方法
     */
    fun onResume(){}
    /**
     * 跟随activity/fragment生命周期，前提是必须调用{@link TXVideoPlayerView.bindLifecycle}方法
     */
    fun onPause(){}
    /**
     * 触发销毁相关方法回调
     */
    fun detach()
}

interface IPlayerController : TXPlayerListener {
    fun attach(context: Context?, videoMgr: VideoMananger?)
    fun detach()

}

interface IVideoView {
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

interface IBasicController : IController {
    fun setCoverIds(resId: Int)
    fun getViewWidth(): Int
    fun getViewHeight(): Int
    fun showBasicMenuLayout()
    fun hideBasicMenuLayout()
    fun showLoading()
    fun hideLoading()
    fun toggleBaicMenuLayout()
    fun bindSurface()
    fun unBindSurface()
    fun updateNetspeed(speed: Int?)
    fun updatePlayButton(isPlaying: Boolean)
    fun updateTextTime(current: Int?, total: Int?)
    fun updateFullScreen(isFullScreen: Boolean?)
    fun updateSeekBar(progress: Int?, secondaryProgress: Int? = null)
    fun updateMultiple(value: Float)
}
interface ISeekBarController : IController
interface IGestureController : IController {
    fun show(changePercent: Float)
    fun hide()
}
interface IMultipleController : IController {
    fun show()
    fun hide()
    fun toggle()
}
abstract class AbsCustomController: IController {
    private var mView: View? = null
    private var mPlayerView: TXVideoPlayerView? = null
    fun getPlayerView() = mPlayerView
    abstract fun getLayoutResId(): Int
    abstract fun onCreate()

    override fun attach(playerView: TXVideoPlayerView) {
        mPlayerView = playerView
        onCreate()
    }

    override fun detach() {
        mPlayerView = null
        mView = null
    }

    fun inflater(playerView: TXVideoPlayerView): View? {
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