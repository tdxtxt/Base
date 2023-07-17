package com.tdxtxt.liteavplayer.video.inter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.IdRes
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.TXVideoPlayerView
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tdxtxt.liteavplayer.video.bean.BitrateItem
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

    /**
     * 设置显示水印
     */
    fun setWaterMark(dynamicWatermarkTip: String?, tipTextSize: Int, tipTextColor: Int)

    /**
     * 设置title
     */
    fun setTitle(title: CharSequence?)

    /**
     * 设置可拖着百分比进度
     */
    fun setDragMaxPercent(dragMaxPercent: Float?)

    /**
     * 设置倍速选择列表
     */
    fun setMultipleList(multipleList: List<Float>?)

    /**
     * 获取倍速选择列表
     */
    fun getMultipleList(): List<Float>
}

interface IBasicController : IController {
    fun setCoverIds(resId: Int)
    fun getViewWidth(): Int
    fun getViewHeight(): Int
    fun showBasicMenuLayout()
    fun hideBasicMenuLayout()
    fun toggleBaicMenuLayout()
    fun showLoading()
    fun hideLoading()
    fun bindSurface()
    fun unBindSurface()
    fun updateNetspeed(speed: Int?)
    fun updatePlayButton(isPlaying: Boolean)
    fun updateTextTime(current: Int?, total: Int?)
    fun updateFullScreen(isFullScreen: Boolean?)
    fun updateSeekBar(progress: Int?, secondaryProgress: Int? = null)
    fun updateMultiple(value: Float)
    fun updateBitrate(value: BitrateItem?)
}
interface ISeekBarController : IController
interface IGestureController : IController {
    fun show(changePercent: Float)
    fun hide()
}

abstract class AbsPopupWindowController: IController{
    private var mPopupView: View? = null
    private var mPlayerView: TXVideoPlayerView? = null
    private val popupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    abstract fun getLayoutResId(): Int
    /**
     * @isFirstInit true:popup内容view的第一次初始化，用于初始化控件
     * 返回值：true:拦截；false:不拦截
     */
    abstract fun onCreate(isFirstInit: Boolean): Boolean

    /**
     * PopupView 依附的目标view
     */
    abstract fun getAnchorView(): View?

    fun getPlayerView() = mPlayerView

    override fun attach(playerView: TXVideoPlayerView) {
        mPlayerView = playerView
    }

    override fun detach() {
        hide()
    }

    open fun show(){
        val context = mPlayerView?.context?: return
        val isFirstInit = mPopupView == null
        if(isFirstInit) mPopupView = LayoutInflater.from(context).inflate(getLayoutResId(), mPlayerView, false)
        val contentView = mPopupView?: return

        val intercept = onCreate(isFirstInit)
        if(intercept){
            return
        }
        popupWindow.contentView = contentView
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = false
        //设置是否允许PopupWindow的范围超过屏幕范围
        popupWindow.isClippingEnabled = false

        //PopupWindow在targetView下方弹出
        val targetView = getAnchorView()?: return

        val targetTocation = IntArray(2)
        targetView.getLocationInWindow(targetTocation)
        val targetX = targetTocation[0]
        val targetY = targetTocation[1]

        //执行measure操作，以便后面获取高宽
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val contentWidth = contentView.measuredWidth
        val contentHeight = contentView.measuredHeight
        popupWindow.showAtLocation(targetView, Gravity.NO_GRAVITY,
            targetX  + targetView.width / 2 - contentWidth / 2,
            targetY - contentHeight - LiteavPlayerUtils.dp2px(3f))
    }

    open fun hide(){
        popupWindow.dismiss()
    }

    fun isShowing() = popupWindow.isShowing

    open fun toggle(){
        if(isShowing()){
            hide()
        }else{
            show()
        }
    }

    fun <T : View> findViewById(@IdRes id: Int): T? {
        return mPopupView?.findViewById(id)
    }
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

    open fun inflater(playerView: TXVideoPlayerView): View? {
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