package com.tdxtxt.video.kernel.impl.exo

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.view.SurfaceView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.video.VideoSize
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer
import com.tdxtxt.video.utils.PlayerUtils

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/23
 *     desc   :
 * </pre>
 */
class ExoMediaPlayer(val context: Context?) : AbstractVideoPlayer(), Player.Listener {
    private var mMediaPlayer: ExoPlayer? = null
    private var mPath: String? = null
    private var multiple = 1f
    private var mLastSecond = 0
    private val mIntervalTime = 1000L
    private val mPlayerProgressRunnable = Runnable {
        val second = (getCurrentDuration() / 1000L).toInt()
        if(mLastSecond != second){
            sendPlayingEvent(second)
            mLastSecond = second
        }
        runProgress()
    }

    init {
        initPlayer()
    }

    override fun initPlayer() {
        try{
            if(context == null) return
            mMediaPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(context))
                .build()
        }catch (e: Exception){}

        mMediaPlayer?.addListener(this)
        mMediaPlayer?.addAnalyticsListener(EventLogger(null, "video"))
        mMediaPlayer?.setAudioAttributes(AudioAttributes.DEFAULT,  true)
        mMediaPlayer?.playWhenReady = false
        mMediaPlayer?.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
    }

    private fun runProgress(){
        PlayerUtils.postRunnable(mPlayerProgressRunnable, mIntervalTime)
    }

    private fun stopProgress(){
        PlayerUtils.removeRunnable(mPlayerProgressRunnable)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState){
            Player.STATE_IDLE -> sendStopEvent() //这是初始状态，即播放器停止和播放失败时的状态。
            Player.STATE_BUFFERING -> sendBufferEvent(0f) //由于需要加载更多数据，播放器无法立即从当前位置播放
            Player.STATE_READY -> {//播放器可以立即从当前位置播放。
                sendBufferEvent(1f)
                sendPreparedEvent()
            }
            Player.STATE_ENDED -> {//播放器播放完所有媒体
                sendCompleteEvent()
                mMediaPlayer?.pause()
            }
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        sendVideoSizeChangedEvent(videoSize.width, videoSize.height)
    }

    override fun onPlayerError(error: PlaybackException) {
        sendErrorEvent("errorcode:${error.errorCode}; errormssage:${error.errorCodeName}")
    }

    override fun bindSurface(surfaceView: SurfaceView) {
        unbindSurface()
         mMediaPlayer?.setVideoSurfaceView(surfaceView)
    }

    override fun unbindSurface() {
        mMediaPlayer?.clearVideoSurface()
    }

    override fun getVideoWidth(): Int {
        return mMediaPlayer?.videoSize?.width?: 0
    }

    override fun getVideoHeight(): Int {
        return mMediaPlayer?.videoSize?.height?: 0
    }

    override fun setDataSource(path: String?) {
        if(mPath == path) return

        path?.let {
            mPath = it
            mMediaPlayer?.setMediaItem(MediaItem.fromUri(path))
            mMediaPlayer?.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        }
        prepare()
    }

    override fun setDataSource(fd: AssetFileDescriptor?) {
        TODO("Not yet implemented")
    }

    override fun prepare() {
        mMediaPlayer?.prepare()
    }

    override fun start() {
        val current = getCurrentDuration()
        val total = getDuration()
        if(Math.abs(total - current) < 1000 && current > 2000){
            seekTo(current - 2000)
        }
        if(!isPlaying()) sendStartEvent()
        mMediaPlayer?.play()
        stopProgress()
        runProgress()
    }

    override fun reStart() {
        sendStartEvent()
        mMediaPlayer?.play()
        stopProgress()
        runProgress()
    }

    override fun pause() {
        if(isPlaying()) sendPauseEvent()
        mMediaPlayer?.pause()
        stopProgress()
    }

    override fun togglePlay() {
        if(isPlaying()){
            pause()
        }else{
            start()
        }
    }

    override fun stop() {
        mMediaPlayer?.stop()
        mMediaPlayer?.clearMediaItems()
        sendStopEvent()
        stopProgress()
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying?: false
    }

    override fun seekTo(time: Long) {
        if(time > getDuration()){
            mMediaPlayer?.seekTo(getDuration())
        }else if(time < 0){
            mMediaPlayer?.seekTo(0)
        }else{
            mMediaPlayer?.seekTo(time)
        }
    }

    override fun accurateSeekTo(time: Long) {
        if(time > getDuration()){
            mMediaPlayer?.seekTo(getDuration())
        }else if(time < 0){
            mMediaPlayer?.seekTo(0)
        }else{
            mMediaPlayer?.seekTo(time)
        }
    }

    override fun release() {
        stopProgress()
        stop()
        sendReleaseEvent()
        mMediaPlayer?.release()
        mMediaPlayer = null
        setPlayerEventListener(null)
        removePlayerEventListener()
    }

    override fun isRelease(): Boolean {
        return mMediaPlayer == null
    }

    override fun getCurrentDuration(): Long {
        return mMediaPlayer?.currentPosition?: 0
    }

    override fun getDuration(): Long {
        return mMediaPlayer?.duration?: 0
    }

    override fun getBufferedPercentage(): Int {
        val totalLength = getDuration()
        if(totalLength == 0L) return 0
        val buffer = mMediaPlayer?.bufferedPosition?: 0
        val rate = (buffer / totalLength) * 100
        return rate.toInt()
    }

    override fun setVolume(volume: Float) {
        mMediaPlayer?.volume = volume
    }

    override fun getVolume(): Float {
        return mMediaPlayer?.volume?: 0f
    }

    override fun setLooping(isLooping: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setSpeed(speed: Float) {
        multiple = speed
        mMediaPlayer?.setPlaybackSpeed(speed)
        sendMultipleChangeEvent(speed)
    }

    override fun getSpeed(): Float {
        return multiple
    }

    override fun getTcpSpeed(): Long {
        TODO("Not yet implemented")
    }


}