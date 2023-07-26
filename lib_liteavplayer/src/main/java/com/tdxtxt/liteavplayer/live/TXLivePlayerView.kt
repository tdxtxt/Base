package com.tdxtxt.liteavplayer.live

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleObserver
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.live.controller.GestureController
import com.tdxtxt.liteavplayer.live.controller.NetworkController
import com.tdxtxt.liteavplayer.live.controller.OrientationController
import com.tdxtxt.liteavplayer.live.controller.view.BasicControllerView
import com.tdxtxt.liteavplayer.live.controller.view.BrightControllerView
import com.tdxtxt.liteavplayer.live.controller.view.VolumeControllerView
import com.tdxtxt.liteavplayer.live.inter.AbsCustomController
import com.tdxtxt.liteavplayer.live.inter.IController
import com.tdxtxt.liteavplayer.live.inter.ILivePlayer
import com.tdxtxt.liteavplayer.live.inter.ILiveView
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tencent.live2.V2TXLivePlayer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/15
 *     desc   : 直播播放器 仅支持：RTMP播放流(rtmp 延迟较低、高并发有压力，1s左右延迟) WebRTC播放流(webrtc 延迟最低，低于1s延迟) FLV播放流(flv 成熟度高、高并发无压力，3s左右延迟)
 * </pre>
 */
class TXLivePlayerView : FrameLayout, ILiveView, ILivePlayer, LifecycleObserver {
    private var mLiveMgr: LiveMananger? = null
    private var mWidthRatio = -1
    private var mHeightRatio = -1
    private lateinit var mBaicView: BasicControllerView
    private lateinit var mGestureController: GestureController
    private lateinit var mOrientationController: OrientationController
    private lateinit var mNetworkController: NetworkController
    private lateinit var mVolumeControllerView: VolumeControllerView
    private lateinit var mBrightControllerView: BrightControllerView
    private val mControllerList: MutableList<IController> = ArrayList()

    private var mOrientationType = OrientationController.VERITCAL
    private var mFullChangelisenter: ((isFullScreen: Boolean) -> Unit)? = null

    fun setLiveManager(manager: LiveMananger){
        mLiveMgr = manager

        getBaicView().bindSurface()
    }
    fun getLiveManager() = mLiveMgr

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        if(attrs != null){
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.TXLivePlayerView)
            mWidthRatio = attributes.getInteger(R.styleable.TXLivePlayerView_tx_width_ratio, -1)
            mHeightRatio = attributes.getInteger(R.styleable.TXLivePlayerView_tx_height_ratio, -1)
            attributes.recycle()
        }
        initView(context)
    }

    private fun initView(context: Context){
        mBaicView = BasicControllerView(context)
        mBaicView.attach(this)
        mControllerList.add(mBaicView)

        mOrientationController = OrientationController()
        mOrientationController.attach(this)
        mControllerList.add(mOrientationController)

        mGestureController = GestureController()
        mGestureController.attach(this)
        mControllerList.add(mGestureController)

        mNetworkController = NetworkController()
        mNetworkController.attach(this)
        mControllerList.add(mNetworkController)

        mVolumeControllerView = VolumeControllerView(context)
        mVolumeControllerView.attach(this)
        mControllerList.add(mVolumeControllerView)

        mBrightControllerView = BrightControllerView(context)
        mBrightControllerView.attach(this)
        mControllerList.add(mBrightControllerView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(mWidthRatio > 0 && mHeightRatio > 0){
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width.toFloat() / mWidthRatio.toFloat() * mHeightRatio.toFloat())
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY))
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    fun getBaicView() = mBaicView
    fun getBrightControllerView() = mBrightControllerView
    fun getVolumeControllerView() = mVolumeControllerView


    override fun getPlayer(): V2TXLivePlayer? {
        return mLiveMgr?.getPlayer()
    }

    override fun setLiveSource(url: String?) {
        mLiveMgr?.setLiveSource(url)
    }

    override fun resume() {
        mLiveMgr?.resume()
    }

    override fun pause() {
        mLiveMgr?.pause()
    }

    override fun release() {
        mLiveMgr?.release()
    }

    override fun showCustomView(iView: AbsCustomController) {
        hideCustomView()
        val view = iView.inflater(this)
        view?.id = R.id.txlive_customview
        if(view != null) getBaicView().addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun hideCustomView() {
        val customView: View? = getBaicView().findViewById(R.id.txlive_customview)
        if(customView != null) getBaicView().removeView(customView)
    }

    override fun onBackPressed(): Boolean {
        if (isFullScreen()) {
            stopFullScreen()
            return false
        }else {
            return true
        }
    }

    override fun back() {
        if(onBackPressed()){
            release()
            getActivity()?.finish()
        }
    }

    override fun isFullScreen(): Boolean {
        return isReverseFullScreen() || isForwardFullScreen()
    }

    override fun stopFullScreen() {
        if(!isFullScreen()) return
        val activity = getActivity() ?: return

        if(activity.isFinishing || activity.isDestroyed) return

        if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        mOrientationType = OrientationController.VERITCAL

        LiteavPlayerUtils.showSysBar(activity)
        mFullChangelisenter?.invoke(isFullScreen())

        getBaicView().updateFullScreen(isFullScreen())
//        getBaicView().moveBasicTopMenuLayout()
        val parentView = getBaicView().parent
        if(parentView is ViewGroup){
            parentView.removeView(getBaicView())
        }
        addView(getBaicView(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    }

    override fun startFullScreen(isReverse: Boolean?) {
        if(isReverseFullScreen() == isReverse) return
        val activity = getActivity() ?: return

        if(activity.isFinishing || activity.isDestroyed) return
        if(isReverse == true || mOrientationController.isReverseHorizontal()){
            mOrientationType = OrientationController.HORIZONTA_REVERSE
            if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
        }else{
            mOrientationType = OrientationController.HORIZONTA_FORWARD
            if(activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }

        LiteavPlayerUtils.hideSysBar(activity)
        mFullChangelisenter?.invoke(isFullScreen())

        getBaicView().updateFullScreen(isFullScreen())
        val parentView = getBaicView().parent
        if(parentView is ViewGroup){
            parentView.removeView(getBaicView())
        }
        val dectorView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        dectorView.addView(getBaicView(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun isReverseFullScreen() = mOrientationType == OrientationController.HORIZONTA_REVERSE
    fun isForwardFullScreen() = mOrientationType == OrientationController.HORIZONTA_FORWARD

    fun getActivity(): Activity? {
        val activity = context
        if(activity is Activity) return activity
        return null
    }

    private fun destoryView(){
        mControllerList.forEach {
            it.detach()
        }
        mControllerList.clear()
        mFullChangelisenter = null
    }

}