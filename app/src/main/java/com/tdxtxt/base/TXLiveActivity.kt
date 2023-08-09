package com.tdxtxt.base

import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.CommToolBarActivity
import com.tdxtxt.liteavplayer.LiteAVManager
import kotlinx.android.synthetic.main.activity_txlive_test.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/12
 *     desc   :
 * </pre>
 */
class TXLiveActivity : CommToolBarActivity() {
    override fun getLayoutResId() = R.layout.activity_txlive_test

    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        livePlayer.setLiveManager(LiteAVManager.getLiveManage())
//        livePlayer.setLiveSource("rtmp://liteavapp.qcloud.com/live/liteavdemoplayerstreamid")
//        livePlayer.setLiveSource("webrtc://liteavapp.qcloud.com/live/liteavdemoplayerstreamid")
//        livePlayer.setLiveSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.flv")
        livePlayer.setLiveSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.m3u8")
    }

    override fun onBackPressed() {
        if(livePlayer.onBackPressed()){
            super.onBackPressed()
        }
    }

}