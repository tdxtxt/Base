package com.tdxtxt.liteavplayer

import android.app.Application
import android.content.Context
import android.util.SparseArray
import com.tdxtxt.liteavplayer.live.LiveMananger
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tencent.liteav.base.util.LiteavLog
import com.tencent.rtmp.TXLiveBase
import com.tencent.rtmp.TXLiveBaseListener
import com.tencent.rtmp.TXVodPlayConfig
import com.tencent.rtmp.TXVodPlayer


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
object LiteAVManager {
    private val defalutKeyId = 0
    private var videoMgrMap: SparseArray<VideoMananger> = SparseArray()
    private var liveMgrMap: SparseArray<LiveMananger> = SparseArray()
    private var mApp: Application? = null
    private var mReferer: String? = null

    /**
     * referer 防止 URL 被到处拷贝的 Referer 字段（腾讯云可以提供更加安全的签名防盗链方案），如没有特殊需求，可以不传
     */
    fun init(app: Application, licenceURL: String?, licenceKey: String?, referer: String? = null){
        mApp = app
        mReferer = referer
        TXLiveBase.getInstance().setLicence(app.applicationContext, licenceURL, licenceKey)
        TXLiveBase.setListener(object : TXLiveBaseListener() {
            override fun onLicenceLoaded(result: Int, reason: String) {
                LiteavLog.i("TXVodPlayer", "onLicenceLoaded：result = $result; reason = $reason")
            }
        })
    }

    fun getReferer(): String? {
        return mReferer
    }

    fun getVideoManage(keyId: Int = defalutKeyId, context: Context? = mApp?: LiteavPlayerUtils.getApplicationByReflect(), config: ((player: TXVodPlayer?, TXVodPlayConfig) -> Unit)? = null): VideoMananger {
        if (videoMgrMap.indexOfKey(keyId) < 0) {
            videoMgrMap.put(keyId, VideoMananger(context, keyId, config))
        }

        if (videoMgrMap.get(keyId).isRelease()) {
            videoMgrMap.put(keyId, VideoMananger(context, keyId, config))
        }
        return videoMgrMap.get(keyId)
    }

    fun getLiveManage(keyId: Int = defalutKeyId, context: Context? = mApp?: LiteavPlayerUtils.getApplicationByReflect()): LiveMananger {
        if (liveMgrMap.indexOfKey(keyId) < 0) {
            liveMgrMap.put(keyId, LiveMananger(context, keyId))
        }

        return liveMgrMap.get(keyId)
    }

    fun removeAllVideoManager(){
        videoMgrMap.clear()
    }

    fun removeVideoManager(key: Int){
        videoMgrMap.remove(key)
    }

    fun removeLiveManager(key: Int){
        liveMgrMap.remove(key)
    }

}