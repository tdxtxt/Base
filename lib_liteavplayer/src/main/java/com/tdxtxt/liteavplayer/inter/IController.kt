package com.tdxtxt.liteavplayer.weight.inter

import android.content.res.AssetFileDescriptor
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import com.tdxtxt.liteavplayer.inter.IVideoPlayer
import com.tdxtxt.liteavplayer.weight.TXVideoPlayerView
import com.tencent.rtmp.TXVodPlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
interface IController {
    fun attach(playerView: TXVideoPlayerView)
    fun detach()
}

interface IVideoView {
    /**
     * 显示自定义view
     */
    fun showCustomView(iView: AbsControllerCustom)

    /**
     * 隐藏自定义view
     */
    fun hideCustomView()
    /**
     * 是否可返回
     */
    fun onBackPressed(): Boolean

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

interface IBaiscController : IController {
    fun setCoverIds(resId: Int)
    fun getViewWidth(): Int
    fun getViewHeight(): Int
    fun showBaicMenuLayout()
    fun hideBaicMenuLayout()
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
}
interface ISeekBarController : IController
interface IGestureController : IController{
    fun show(changePercent: Float)
    fun hide()
}
interface IMultipleController : IController{
    fun show()
    fun hide()
}
abstract class AbsControllerCustom: IController{
    private var mView: View? = null
    abstract fun getLayoutResId(): Int

    override fun detach() {
        mView = null
    }

    fun init(playerView: TXVideoPlayerView): View? {
        mView = LayoutInflater.from(playerView.context).inflate(getLayoutResId(), null, false)
        //禁止手势传递
        mView?.setOnTouchListener { v, event ->  true}
        attach(playerView)
        return mView
    }

    fun <T : View> findViewById(@IdRes id: Int): T? {
        return mView?.findViewById(id)
    }

}