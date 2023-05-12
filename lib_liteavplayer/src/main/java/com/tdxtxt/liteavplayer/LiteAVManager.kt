package com.tdxtxt.liteavplayer

import android.app.Application
import android.content.Context
import android.os.Bundle
import com.tdxtxt.liteavplayer.inter.IVideoPlayer
import com.tdxtxt.liteavplayer.inter.TXPlayerListener
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tencent.rtmp.*


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
class LiteAVManager : IVideoPlayer {
    private var isDestory = false
    private var mPlayerEventListenerListRef: MutableList<TXPlayerListener>? = null
    private var mPlayer: TXVodPlayer? = null
    private var mMultipleSpeed = 1f //倍速

    companion object{
        private val mInstance: LiteAVManager by lazy { LiteAVManager() }
        private var mApp: Application? = null

        fun init(app: Application, licenceURL: String?, licenceKey: String?){
            mApp = app
            TXLiveBase.getInstance().setLicence(app.applicationContext, licenceURL, licenceKey)
            TXLiveBase.setListener(object : TXLiveBaseListener() {
                override fun onLicenceLoaded(result: Int, reason: String) {}
            })
        }

        fun newInstance(context: Context? = mApp?: LiteavPlayerUtils.getApplicationByReflect()): LiteAVManager {
            return mInstance.apply {
                if(mPlayer == null){
                    isDestory = false
                    mPlayer = TXVodPlayer(context)
                    configPlayer(this)
                }

                if(isRelease()){
                    isDestory = false
                    mPlayer = TXVodPlayer(context)
                    configPlayer(this)
                }
            }
        }

        private fun configPlayer(manager: LiteAVManager){
            manager.mPlayer?.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
            manager.mPlayer?.setBitrateIndex(-1) //SDK 支持 HLS 的多码流自适应，开启相关能力后播放器能够根据当前带宽，动态选择最合适的码率播放
            manager.mPlayer?.setConfig(TXVodPlayConfig().apply {
                setSmoothSwitchBitrate(true) //开启平滑切换码率
                setProgressInterval(1000)  // 设置进度回调间隔，单位毫秒
            })
            manager.mPlayer?.setVodListener(object : ITXVodPlayListener{
                override fun onPlayEvent(player: TXVodPlayer?, event: Int, param: Bundle?) {
                    if(event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED){
                        manager.sendPreparedEvent()
                    }else if(event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN){
                        manager.sendStartEvent()
                    }else if(event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS){
                        val playProgress = param?.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)?.let { it / 1000 }
                        manager.sendPlayingEvent(playProgress?: 0)
                    }else if(event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
                        manager.sendLoadingEvent(true)
                    }else if(event == TXLiveConstants.PLAY_EVT_VOD_LOADING_END){
                        manager.sendLoadingEvent(false)
                    }else if(event == TXLiveConstants.PLAY_EVT_PLAY_END){
                        manager.sendCompleteEvent()
                    }
                }
                override fun onNetStatus(player: TXVodPlayer?, param: Bundle?) {
                    // 获取实时速率, 单位：kbps
                    val speed = param?.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)
                    manager.sendNetspeedEvent(speed)
                }

            })
        }
    }

    /******************************************************单独的接口开始*************************************************************************/
    fun setToken(token: String?){
        getPlayer()?.setToken(token)
    }

    fun addPlayerEventListener(listener: TXPlayerListener?){
        if(listener == null) return
        if(mPlayerEventListenerListRef == null) mPlayerEventListenerListRef = ArrayList()
        mPlayerEventListenerListRef?.add(listener)
    }

    fun removeEventListener(listener: TXPlayerListener?){
        if(listener == null) return
        mPlayerEventListenerListRef?.remove(listener)
    }

    /******************************************************播放接口开始*************************************************************************/
    override fun getPlayer(): TXVodPlayer? {
        return mPlayer
    }

    override fun setDataSource(appId: Int, fileId: String?, psign: String?, startTime: Int?, autoPlay: Boolean) {
        if(startTime != null) getPlayer()?.setStartTime(startTime.toFloat())
        getPlayer()?.setAutoPlay(autoPlay)
        val playInfo = TXPlayInfoParams(appId, fileId, psign)
        getPlayer()?.startVodPlay(playInfo)
    }

    override fun setDataSource(path: String?, startTime: Int?, autoPlay: Boolean) {
        if (startTime != null) getPlayer()?.setStartTime(startTime.toFloat())
        getPlayer()?.setAutoPlay(autoPlay)
        getPlayer()?.startVodPlay(path)
    }

    override fun reStart() {
        getPlayer()?.setStartTime(0f)
        resume()
    }

    override fun resume() {
        getPlayer()?.resume()
    }

    override fun pause() {
        sendPauseEvent()//因为SDK没有暂停这一事件，所以就自己实现
        getPlayer()?.pause()
    }

    override fun togglePlay() {
        if(isPlaying()){
            pause()
        }else{
            resume()
        }
    }

    override fun isPlaying(): Boolean {
        return getPlayer()?.isPlaying == true
    }

    override fun seekTo(time: Int) {
        getPlayer()?.seek(time)
    }

    override fun release() {
        getPlayer()?.stopPlay(true) // true 代表清除最后一帧画面
        mPlayer = null
        isDestory = true
        sendReleaseEvent()
        mPlayerEventListenerListRef?.clear()
    }

    override fun isRelease() = isDestory

    override fun getCurrentDuration(): Int {
        return getPlayer()?.currentPlaybackTime?.toInt()?: 0
    }

    override fun getDuration(): Int {
        return getPlayer()?.duration?.toInt()?: 0
    }

    override fun getCurrentPercentage(): Int {
        val totalTime = getDuration()
        if(totalTime == 0) return 0
        val percent = getCurrentDuration() / totalTime.toFloat()
        if(percent > 1) return 100
        return (percent * 100).toInt()
    }

    override fun getBufferedPercentage(): Int {
        val totalTime = getDuration()
        if(totalTime == 0) return 0
        val percent = getBufferedDuration() / totalTime.toFloat()
        if(percent > 1) return 100
        return (percent * 100).toInt()
    }

    override fun getBufferedDuration(): Int {
        return getPlayer()?.bufferDuration?.toInt()?: 0
    }

    override fun setLooping(isLooping: Boolean) {
        getPlayer()?.isLoop = isLooping
    }

    override fun setMultiple(speed: Float) {
        mMultipleSpeed = speed
        getPlayer()?.setRate(speed)
    }

    override fun getMultiple(): Float {
        return mMultipleSpeed
    }

    /******************************************************发送播放事件开始*************************************************************************/
    private fun sendPlayerEvent(@TXPlayerListener.PlayerState state: Int, value: Any? = null){
        mPlayerEventListenerListRef?.forEach {
            it.onPlayStateChanged(state, value)
        }
    }

    private fun sendCompleteEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_COMPLETED)
    }

    private fun sendStartEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_START)
    }

    private fun sendPlayingEvent(second: Int){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_PLAYING, second)
    }

    private fun sendLoadingEvent(isLoading: Boolean){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_LOADING, isLoading)
    }

    private fun sendPauseEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_PAUSED)
    }

    private fun sendPreparedEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_PREPARED)
    }

    private fun sendReleaseEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_RELEASE)
    }

    private fun sendNetspeedEvent(speed: Int?){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_NETSPEED, speed)
    }

    private fun sendErrorEvent(error: String?){
        sendPlayerEvent(TXPlayerListener.PlayerState.STATE_ERROR, error)
    }
//    private fun sendVideoSizeChangedEvent(width: Int, height: Int){
//        sendPlayerEvent(TXPlayerListener.PlayerState.CHANGE_VIDEO_SIZE)
//    }
//
//    private fun sendMultipleChangeEvent(value: Float){
//        sendPlayerEvent(TXPlayerListener.PlayerState.CHANGE_MULTIPLE, value)
//    }
}