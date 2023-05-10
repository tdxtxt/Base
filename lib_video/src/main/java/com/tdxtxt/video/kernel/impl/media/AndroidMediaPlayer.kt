package com.tdxtxt.video.kernel.impl.media

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.SurfaceView
import com.tdxtxt.video.kernel.inter.AbstractVideoPlayer

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
class AndroidMediaPlayer constructor(val context: Context?) : AbstractVideoPlayer() {
    private val MEDIA_INFO_VIDEO_RENDERING_START = 3

    private var mMediaPlayer: MediaPlayer? = null
    private var mBufferedPercent = 0
    private var mIsPreparing = false
    private var mIsInit = false

    override fun initPlayer() {
        if(mIsInit) return
        mMediaPlayer = MediaPlayer()
        initListener()
    }

    override fun setDataSource(path: String?) {
        // 设置dataSource
        if (path == null || path.isEmpty()) {
            sendErrorEvent("视频资源路径为空")
            return
        }

        if(context == null){
            sendErrorEvent("context is null")
            return
        }
        try {
            val uri = Uri.parse(path)
            mMediaPlayer?.setDataSource(context, uri, null)
        } catch (e: Exception) {
            sendErrorEvent(e.message)
        }
    }

    override fun setDataSource(fd: AssetFileDescriptor?) {
        try {
            mMediaPlayer?.setDataSource(fd?.fileDescriptor, fd?.startOffset ?: 0, fd?.length ?: 0)
        } catch (e: Exception) {
            sendErrorEvent(e.message)
        }
    }

    override fun prepare() {
        TODO("Not yet implemented")
    }

    override fun start() {
        try {
            initPlayer()
            mMediaPlayer?.start()
        } catch (e: Exception) {
            sendErrorEvent(e.message)        }
    }

    override fun reStart() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        try {
            mMediaPlayer?.pause()
        } catch (e: Exception) {
            sendErrorEvent(e.message)        }
    }

    override fun togglePlay() {
        if(isPlaying()){
            pause()
        }else{
            start()
        }
    }

    override fun stop() {
        try {
            mMediaPlayer?.stop()
        } catch (e: Exception) {
            sendErrorEvent(e.message)        }
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }

    override fun seekTo(time: Long) {
        try {
            mMediaPlayer?.seekTo(time.toInt())
        } catch (e: Exception) {
            sendErrorEvent(e.message)        }
    }

    override fun accurateSeekTo(time: Long) {
        TODO("Not yet implemented")
    }

    override fun release() {
        mMediaPlayer?.setOnErrorListener(null)
        mMediaPlayer?.setOnCompletionListener(null)
        mMediaPlayer?.setOnInfoListener(null)
        mMediaPlayer?.setOnBufferingUpdateListener(null)
        mMediaPlayer?.setOnPreparedListener(null)
        mMediaPlayer?.setOnVideoSizeChangedListener(null)
        mMediaPlayer?.release()//是否需要再子线程中释放呢？
        mMediaPlayer = null
    }

    override fun isRelease(): Boolean {
        return mMediaPlayer == null
    }

    override fun getCurrentDuration(): Long {
        return (mMediaPlayer?.currentPosition ?: 0).toLong()
    }

    override fun getDuration(): Long {
        return (mMediaPlayer?.duration ?: 0).toLong()
    }

    override fun getCurrentPercentage(): Int {
        val totalTime = getDuration()
        if(totalTime == 0L) return 0
        val percent = getCurrentDuration() / totalTime.toFloat()
        if(percent > 1) return 100
        return (percent * 100).toInt()
    }

    override fun getBufferedPercentage(): Int {
        return mBufferedPercent
    }

    override fun getBufferedDuration(): Long {
        return mBufferedPercent.toLong()
    }

    override fun bindSurface(surfaceView: SurfaceView) {
        try {
            mMediaPlayer?.setSurface(surfaceView.holder.surface)
//            mMediaPlayer?.setDisplay(surfaceView.holder)
        } catch (e: Exception) {
            sendErrorEvent(e.message)
        }
    }

    override fun unbindSurface() {
        mMediaPlayer?.setSurface(null)
        mMediaPlayer?.setDisplay(null)
    }

    override fun getVideoWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getVideoHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun setVolume(volume: Float) {
        try {
            mMediaPlayer?.setVolume(volume, volume)
        } catch (e: Exception) {
            sendErrorEvent(e.message)
        }
    }

    override fun getVolume() = 1f

    override fun setLooping(isLooping: Boolean) {
        try {
            mMediaPlayer?.setLooping(isLooping)
        } catch (e: Exception) {
            sendErrorEvent(e.message)
        }
    }

    override fun setSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mMediaPlayer?.playbackParams?.apply {
                    setSpeed(speed)
                    mMediaPlayer?.playbackParams = this
                    sendMultipleChangeEvent(speed)
                }
            } catch (e: Exception) {
                sendErrorEvent(e.message)
            }
        }
    }

    override fun getSpeed(): Float {
        var speed = 1F
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                speed = mMediaPlayer?.playbackParams?.speed?: 1F
            } catch (e: Exception) {
                sendErrorEvent(e.message)
            }
        }
        return speed
    }

    override fun getTcpSpeed(): Long {
        // no support
        return 0
    }

    private val onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        sendErrorEvent("监听异常$what, extra: $extra")
        true
    }

    private val onCompletionListener =
        MediaPlayer.OnCompletionListener { sendCompleteEvent() }

    private val onInfoListener =
        MediaPlayer.OnInfoListener { mp, what, extra -> //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
            if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
                if (mIsPreparing) {
                    sendBufferEvent(0f)
                    mIsPreparing = false
                }
            } else {
                sendBufferEvent(0f)
            }
            true
        }

    private val onBufferingUpdateListener =
        MediaPlayer.OnBufferingUpdateListener { mp, percent -> mBufferedPercent = percent }

    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        sendBufferEvent(1f)
        start()
    }

    private val onVideoSizeChangedListener =
        MediaPlayer.OnVideoSizeChangedListener { mp, width, height ->
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            if (videoWidth != 0 && videoHeight != 0) {
//                onVideoSizeChanged(videoWidth, videoHeight)
            }
        }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private fun initListener() {
        mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer?.setOnErrorListener(onErrorListener)
        mMediaPlayer?.setOnCompletionListener(onCompletionListener)
        mMediaPlayer?.setOnInfoListener(onInfoListener)
        mMediaPlayer?.setOnBufferingUpdateListener(onBufferingUpdateListener)
        mMediaPlayer?.setOnPreparedListener(onPreparedListener)
        mMediaPlayer?.setOnVideoSizeChangedListener(onVideoSizeChangedListener)
    }
}