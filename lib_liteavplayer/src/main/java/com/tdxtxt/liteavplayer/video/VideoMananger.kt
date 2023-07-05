package com.tdxtxt.liteavplayer.video

import android.content.Context
import android.os.Bundle
import com.tdxtxt.liteavplayer.LiteAVManager
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.utils.NetworkState
import com.tdxtxt.liteavplayer.video.controller.PlayerAudioFocusController
import com.tdxtxt.liteavplayer.video.controller.PlayerHeadsetController
import com.tdxtxt.liteavplayer.video.inter.IPlayerController
import com.tdxtxt.liteavplayer.video.inter.IVideoPlayer
import com.tdxtxt.liteavplayer.video.inter.TXPlayerListener
import com.tencent.rtmp.*
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   :
 * </pre>
 */
class VideoMananger constructor(val context: Context?, val id: Long) : IVideoPlayer {
    private var isDestory = false
    private var mPlayerEventListenerListRef: MutableList<TXPlayerListener>? = null
    private var mPlayer: TXVodPlayer? = null
    private var mDataSource: String? = null
    private var mMultipleSpeed = 1f //倍速
    private var mLastStartPlayTime = 0L //上一次开始播放视频的时间戳，主要用来防止用户连续不断的触发播放，导致播放器卡死
    private val mControllerList: MutableList<IPlayerController> = ArrayList()
    init {
        mPlayer = TXVodPlayer(context)
        configPlayer(mPlayer)

        val audioFocusController = PlayerAudioFocusController()
        audioFocusController.attach(context, this)
        mControllerList.add(audioFocusController)
        val headsetController = PlayerHeadsetController()
        headsetController.attach(context, this)
        mControllerList.add(headsetController)
    }

    private fun configPlayer(player: TXVodPlayer?){
        player?.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        player?.setBitrateIndex(-1) //SDK 支持 HLS 的多码流自适应，开启相关能力后播放器能够根据当前带宽，动态选择最合适的码率播放
        player?.setConfig(TXVodPlayConfig().apply {
            val referer = LiteAVManager.getReferer()
            if(referer != null){
                headers = HashMap<String, String>().apply {
                    put("Referer", referer)
                }
            }
            isSmoothSwitchBitrate = true //开启平滑切换码率
            progressInterval = 1000  // 设置进度回调间隔，单位毫秒
//                maxBufferSize = 100 // 播放时最大缓冲大小。单位：MB
        })
        player?.setVodListener(object : ITXVodPlayListener {
            override fun onPlayEvent(player: TXVodPlayer?, event: Int, param: Bundle?) {
                if(event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED){
                    sendPreparedEvent()
                }else if(event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN){
                    sendStartEvent()
                }else if(event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS){
                    // 播放进度, 单位是秒
                    val playProgress = param?.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)?.let { it / 1000 }
                    // 加载进度, 单位是秒
                    val playableProgress = param?.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS)?.let { it / 1000 }
                    sendPlayingEvent(Pair(playProgress?: 0, playableProgress?: 0))
                }else if(event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
                    sendLoadingEvent(true)
                }else if(event == TXLiveConstants.PLAY_EVT_VOD_LOADING_END){
                    sendLoadingEvent(false)
                }else if(event == TXLiveConstants.PLAY_EVT_PLAY_END){
                    sendCompleteEvent()
                }else{
                    sendUnkownEvent(Pair(event, param))
                }
            }
            override fun onNetStatus(player: TXVodPlayer?, param: Bundle?) {
                // 获取实时速率, 单位：kbps
                val speed = param?.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)
                sendNetspeedEvent(speed)
            }
        })
}

    /******************************************************单独的接口开始*************************************************************************/
    override fun setToken(token: String?){
        getPlayer()?.setToken(token)
    }

    override fun addPlayerEventListener(listener: TXPlayerListener?){
        if(listener == null) return
        if(mPlayerEventListenerListRef == null) mPlayerEventListenerListRef = ArrayList()
        mPlayerEventListenerListRef?.add(listener)
    }

    override fun removeEventListener(listener: TXPlayerListener?){
        if(listener == null) return
        mPlayerEventListenerListRef?.remove(listener)
    }

    /******************************************************播放接口开始*************************************************************************/
    private var mPlayRunnable: Runnable? = null
    override fun getPlayer(): TXVodPlayer? {
        return mPlayer
    }

    override fun setFileIdSource(appId: Int, fileId: String?, psign: String?, startTime: Int?, autoPlay: Boolean) {
        stop(true)//这里将之前的播放内容清除掉，不然频繁调用将会导致播放器卡死
        val playRunnable = Runnable {
            if(startTime != null) getPlayer()?.setStartTime(startTime.toFloat())
            getPlayer()?.setAutoPlay(autoPlay)
            val playInfo = TXPlayInfoParams(appId, fileId, psign)
            getPlayer()?.startVodPlay(playInfo)
        }

        if(abs(System.currentTimeMillis() - mLastStartPlayTime) > 200){//间隔800毫秒，直接播放
            mLastStartPlayTime = System.currentTimeMillis()
            playRunnable.run()
            mPlayRunnable = null
        }else{
            mPlayRunnable?.apply { LiteavPlayerUtils.removeRunnable(this) }
            LiteavPlayerUtils.postRunnable(playRunnable, 200)
            mPlayRunnable = playRunnable
        }
    }

    override fun setDataSource(path: String?, startTime: Int?, autoPlay: Boolean) {
        stop(true)//这里将之前的播放内容清除掉，不然频繁调用将会导致播放器卡死
        val playRunnable = Runnable {
            if (startTime != null) getPlayer()?.setStartTime(startTime.toFloat())
            getPlayer()?.setAutoPlay(autoPlay)
            getPlayer()?.startVodPlay(path)
            mDataSource = path
        }

        if(abs(System.currentTimeMillis() - mLastStartPlayTime) > 200){//间隔200毫秒，直接播放
            mLastStartPlayTime = System.currentTimeMillis()
            playRunnable.run()
            mPlayRunnable = null
        }else{
            mPlayRunnable?.apply { LiteavPlayerUtils.removeRunnable(this) }
            LiteavPlayerUtils.postRunnable(playRunnable, 200)
            mPlayRunnable = playRunnable
        }
    }

    override fun getDataSource() = mDataSource

    override fun reStart() {
        getPlayer()?.setStartTime(0f)
        resume()
    }

    override fun resume() {
        getPlayer()?.resume()
    }

    override fun pause() {
        getPlayer()?.pause()
        if(isPlaying()){
            sendPauseEvent()//因为SDK没有暂停这一事件，所以就自己实现
        }
    }

    override fun stop(clearFrame: Boolean) {
        mDataSource = null
        getPlayer()?.stopPlay(clearFrame)
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
        stop(true)
        mControllerList.forEach { it.detach() }
        mPlayer = null
        mDataSource = null
        isDestory = true
        sendReleaseEvent()
        mPlayerEventListenerListRef?.clear()
        LiteAVManager.clearVideoManager()
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
        sendMultipleChangeEvent(speed)
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

        mControllerList.forEach {
            it.onPlayStateChanged(state, value)
        }
    }

    private fun sendCompleteEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_COMPLETED)
    }

    private fun sendStartEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_START)
    }

    private fun sendPlayingEvent(timePair: Pair<Int, Int>){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_PLAYING, timePair)
    }

    private fun sendLoadingEvent(isLoading: Boolean){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_LOADING, isLoading)
    }

    private fun sendPauseEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_PAUSED)
    }

    private fun sendPreparedEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_PREPARED)
    }

//    private fun sendStopEvent(){
//        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_STOP)
//    }

    private fun sendReleaseEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_RELEASE)
    }

    private fun sendNetspeedEvent(speed: Int?){
        sendPlayerEvent(TXPlayerListener.PlayerState.CHANGE_NETSPEED, speed)
    }

    private fun sendUnkownEvent(eventPair: Pair<Int, Bundle?>){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_UNKOWN, eventPair)
    }

    private fun sendMultipleChangeEvent(value: Float){
        sendPlayerEvent(TXPlayerListener.PlayerState.CHANGE_MULTIPLE, value)
    }

    fun sendNetworkEvent(state: NetworkState){
        sendPlayerEvent(TXPlayerListener.PlayerState.CHANGE_NETWORK, state)
    }

    fun sendNondragEvent(){
        sendPlayerEvent(TXPlayerListener.PlayerState.EVENT_NONDRAG)
    }
}