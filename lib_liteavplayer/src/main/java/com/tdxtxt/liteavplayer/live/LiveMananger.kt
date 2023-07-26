package com.tdxtxt.liteavplayer.live

import android.content.Context
import android.os.Bundle
import com.tdxtxt.liteavplayer.live.inter.ILivePlayer
import com.tencent.liteav.base.util.LiteavLog
import com.tencent.live2.V2TXLiveDef
import com.tencent.live2.V2TXLivePlayer
import com.tencent.live2.V2TXLivePlayerObserver
import com.tencent.live2.impl.V2TXLivePlayerImpl

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/16
 *     desc   : 仅支持：RTMP播放流(rtmp 延迟较低、高并发有压力，1s左右延迟) WebRTC播放流(webrtc 延迟最低，低于1s延迟) FLV播放流(flv 成熟度高、高并发无压力，3s左右延迟)
 *              不支持：HLS(m3u8 延迟较高20s左右延迟)，需要用点播播放器进行播放
 * </pre>
 */
class LiveMananger constructor(val context: Context?, val id: Int): ILivePlayer {
    private var mPlayer: V2TXLivePlayer? = null
    init {
        mPlayer = V2TXLivePlayerImpl(context)
        configPlayer(mPlayer)
    }

    private fun configPlayer(livePlayer: V2TXLivePlayer?){
        //延时调节: 1.自动模式setCacheParams(1.0f, 5.0f) 2.极速模式 setCacheParams(1.0f, 1.0f) 3.流畅模式setCacheParams(5.0f, 5.0f)
        livePlayer?.setCacheParams(1.0f, 5.0f)
        //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边
        livePlayer?.setRenderFillMode(V2TXLiveDef.V2TXLiveFillMode.V2TXLiveFillModeFit)
        livePlayer?.setObserver(object : V2TXLivePlayerObserver(){
            override fun onConnected(player: V2TXLivePlayer?, extraInfo: Bundle?) {
                //已经成功连接到服务器
                LiteavLog.i("TXLivePlayer", "onConnected")
            }
            override fun onAudioLoading(player: V2TXLivePlayer?, extraInfo: Bundle?) {
                //音频加载事件
                LiteavLog.i("TXLivePlayer", "onAudioLoading")
            }
            override fun onAudioPlaying(player: V2TXLivePlayer?, firstPlay: Boolean, extraInfo: Bundle?) {
                //音频播放事件
                LiteavLog.i("TXLivePlayer", "onAudioPlaying")
            }
            override fun onError(player: V2TXLivePlayer?, code: Int, msg: String?, extraInfo: Bundle?) {
                //直播播放器错误通知，播放器出现错误时，会回调该通知
                LiteavLog.i("TXLivePlayer", "onAudioPlaying")
            }
            override fun onPlayoutVolumeUpdate(player: V2TXLivePlayer?, volume: Int) {
                //播放器音量大小回调
                LiteavLog.i("TXLivePlayer", "onPlayoutVolumeUpdate")
            }
            override fun onReceiveSeiMessage(player: V2TXLivePlayer?, payloadType: Int, data: ByteArray?) {
                //收到 SEI 消息的回调，发送端通过 V2TXLivePusher 中的 sendSeiMessage 来发送 SEI 消息。
                LiteavLog.i("TXLivePlayer", "onReceiveSeiMessage payloadType = $payloadType")
            }
            override fun onVideoLoading(player: V2TXLivePlayer?, extraInfo: Bundle?) {
                //视频加载事件
                LiteavLog.i("TXLivePlayer", "onVideoLoading")
            }
            override fun onVideoPlaying(player: V2TXLivePlayer?, firstPlay: Boolean, extraInfo: Bundle?) {
                //视频播放事件
                LiteavLog.i("TXLivePlayer", "onVideoPlaying firstPlay = $firstPlay")
            }
        })
    }

    override fun getPlayer(): V2TXLivePlayer? {
        return mPlayer
    }

    override fun setLiveSource(url: String?) {
        getPlayer()?.startLivePlay(url)
    }

    override fun resume() {
        getPlayer()?.resumeAudio()
        getPlayer()?.resumeVideo()
    }

    override fun pause() {
        getPlayer()?.pauseAudio()
        getPlayer()?.pauseVideo()
    }

    override fun release() {
        getPlayer()?.stopPlay()
    }
}