package com.tdxtxt.base

import android.view.View
import com.tdxtxt.base.databinding.ActivityTxliveTestBinding
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.CommToolBarActivity
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.liteavplayer.LiteAVManager

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/12
 *     desc   :
 * </pre>
 */
class TXLiveActivity : CommToolBarActivity(), IViewBinding<ActivityTxliveTestBinding> {
    override fun viewbind(rootView: View): ActivityTxliveTestBinding {
        return ActivityTxliveTestBinding.bind(rootView)
    }
    override fun getLayoutResId() = R.layout.activity_txlive_test

    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        viewbinding().livePlayer.setLiveManager(LiteAVManager.getLiveManage())
//        livePlayer.setLiveSource("rtmp://liteavapp.qcloud.com/live/liteavdemoplayerstreamid")
//        livePlayer.setLiveSource("webrtc://liteavapp.qcloud.com/live/liteavdemoplayerstreamid")
//        livePlayer.setLiveSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.flv")
        viewbinding().livePlayer.setLiveSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.m3u8")
    }

    override fun onBackPressed() {
        if(viewbinding().livePlayer.onBackPressed()){
            super.onBackPressed()
        }
    }

}