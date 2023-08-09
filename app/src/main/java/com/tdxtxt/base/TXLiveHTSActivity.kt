package com.tdxtxt.base

import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.liteavplayer.LiteAVManager
import com.tencent.rtmp.TXVodConstants
import kotlinx.android.synthetic.main.activity_txlive_test_hts.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-09
 *     desc   :
 * </pre>
 */
class TXLiveHTSActivity : BaseActivity() {
    override fun getLayoutResId() = R.layout.activity_txlive_test_hts

    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        videoPlayer.setVideoManager(LiteAVManager.getVideoManage(2) { player, config ->
            config.mediaType = TXVodConstants.MEDIA_TYPE_HLS_LIVE //HLS直播需要添加这个设置，否则第一次无法播放
        })
        btn_1.setOnClickListener {
//            videoPlayer.setLiveStyle()
            videoPlayer.setDataSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.m3u8", autoPlay = true)
        }

    }


    override fun onBackPressed() {
        if(videoPlayer.onBackPressed()){
            super.onBackPressed()
        }
    }

}