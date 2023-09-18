package com.tdxtxt.base

import android.view.View
import com.tdxtxt.base.databinding.ActivityTxliveTestHtsBinding
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.liteavplayer.LiteAVManager
import com.tencent.rtmp.TXVodConstants

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-09
 *     desc   :
 * </pre>
 */
class TXLiveHTSActivity : BaseActivity(), IViewBinding<ActivityTxliveTestHtsBinding> {
    override fun getLayoutResId() = R.layout.activity_txlive_test_hts
    override fun viewbind(rootView: View): ActivityTxliveTestHtsBinding {
        return ActivityTxliveTestHtsBinding.bind(rootView)
    }
    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        viewbinding().videoPlayer.setVideoManager(LiteAVManager.getVideoManage(2) { player, config ->
            config.mediaType = TXVodConstants.MEDIA_TYPE_HLS_LIVE //HLS直播需要添加这个设置，否则第一次无法播放
        })
        viewbinding().btn1.setOnClickListener {
//            videoPlayer.setLiveStyle()
            viewbinding().videoPlayer.setDataSource("http://liteavapp.qcloud.com/live/liteavdemoplayerstreamid.m3u8", autoPlay = true)
        }

    }


    override fun onBackPressed() {
        if(viewbinding().videoPlayer.onBackPressed()){
            super.onBackPressed()
        }
    }

}