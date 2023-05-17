package com.tdxtxt.liteavplayer

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import android.util.SparseArray
import androidx.core.util.valueIterator
import com.tdxtxt.liteavplayer.live.LiveMananger
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils
import com.tdxtxt.liteavplayer.video.VideoMananger
import com.tdxtxt.liteavplayer.video.inter.IVideoPlayer
import com.tdxtxt.liteavplayer.video.inter.TXPlayerListener
import com.tencent.live2.V2TXLivePlayer
import com.tencent.rtmp.*


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
object LiteAVManager {
    private val videoKeyId = 0
    private var videoMgrMap: SparseArray<VideoMananger> = SparseArray()
    private val liveKeyId = 0
    private var liveMgrMap: SparseArray<LiveMananger> = SparseArray()
    private var mApp: Application? = null
    private var mReferer: String? = null

    fun init(app: Application, licenceURL: String?, licenceKey: String?, referer: String? = null){
        mApp = app
        mReferer = referer
        TXLiveBase.getInstance().setLicence(app.applicationContext, licenceURL, licenceKey)
        TXLiveBase.setListener(object : TXLiveBaseListener() {
            override fun onLicenceLoaded(result: Int, reason: String) {}
        })
    }

    fun getReferer(): String? {
        return mReferer
    }

    fun getVideoManage(context: Context? = mApp?: LiteavPlayerUtils.getApplicationByReflect()): VideoMananger {
        if (videoMgrMap.indexOfKey(videoKeyId) < 0) {
            videoMgrMap.put(videoKeyId, VideoMananger(context, System.currentTimeMillis()))
        }

        if (videoMgrMap.get(videoKeyId).isRelease()) {
            videoMgrMap.put(videoKeyId, VideoMananger(context, System.currentTimeMillis()))
        }
        return videoMgrMap.get(videoKeyId)
    }

    fun getLiveManage(context: Context? = mApp?: LiteavPlayerUtils.getApplicationByReflect()): LiveMananger {
        if (liveMgrMap.indexOfKey(liveKeyId) < 0) {
            liveMgrMap.put(liveKeyId, LiveMananger(context, System.currentTimeMillis()))
        }

        return liveMgrMap.get(liveKeyId)
    }

    fun clearVideoManager(){
        videoMgrMap.clear()
    }

}