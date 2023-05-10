package com.tdxtxt.base

import android.view.View
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.CommToolBarActivity
import com.tdxtxt.video.VideoPlayerManager
import com.tdxtxt.video.kernel.inter.VideoPlayerListener
import com.tdxtxt.video.player.VideoPlayerView
import com.tdxtxt.video.player.controller.AbsControllerCustom
import com.tdxtxt.video.utils.PlayerConstant
import kotlinx.android.synthetic.main.activity_video_test.*

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/15
 *     desc   :
 * </pre>
 */
class VideoActivity : CommToolBarActivity(){
    override fun getLayoutResId() = R.layout.activity_video_test

    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        val manager = VideoPlayerManager.newInstance().getVideoPlayer()
        manager.addPlayerEventListener(object : VideoPlayerListener{
            override fun onPlayStateChanged(state: Int, value: Any?) {
                when(state){
                    PlayerConstant.PlaylerState.STATE_COMPLETED -> {
//                        videoPlayer.setDataSource("https://mediaapi.juexiaotime.com/111119蒋四金法考资料/2019年觉晓法考体验营/左宁/39、公诉案件庭前审查、开庭审判前的准备.mp4")
//                        videoPlayer.postDelayed({ videoPlayer.start() }, 200)
                        videoPlayer.showCustomView(AdCustomView())
                    }
                }
            }
        })
        videoPlayer.bindLifecycle(this)
        videoPlayer.setVideoPlayer(manager)
        videoPlayer.setDataSource("https://mediaapi.juexiaotime.com/1111112023年法考资料/肖沛权/内部课/40diwuzhang.mp4")
//        videoPlayer.setTrackMaxPercent(0.5f)
        videoPlayer.start()
    }

    override fun onBackPressed() {
        if(videoPlayer.onBackPressed()){
            super.onBackPressed()
        }
    }
}


private class AdCustomView : AbsControllerCustom() {
    override fun getLayoutResId() = R.layout.view_video_custom

    override fun attach(container: VideoPlayerView) {
        findViewById<View>(R.id.tv_contine)?.setOnClickListener {
            container.reStart()
            container.hideCustomView()
        }
    }

}