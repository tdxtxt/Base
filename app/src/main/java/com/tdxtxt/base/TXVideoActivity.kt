package com.tdxtxt.base

import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.liteavplayer.LiteAVManager
import kotlinx.android.synthetic.main.activity_txvideo_test.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
class TXVideoActivity : BaseActivity() {
    override fun getLayoutResId() = R.layout.activity_txvideo_test

    override fun initStatusBar() {
        StatusBarHelper.setDarkMode(fragmentActivity)
    }

    override fun initUi() {
        videoPlayer.setPlayerManager(LiteAVManager.newInstance(this))
        videoPlayer.setDataSource("https://mediaapi.juexiaotime.com/1111112023年法考资料/肖沛权/内部课/40diwuzhang.mp4", autoPlay = true)
//        videoPlayer.start("https://1307664769.vod2.myqcloud.com/83cdfc9bvodtranscq1307664769/7ad7e450387702302383294626/v.f100230.m3u8?t=645e1f1a&sign=642b98f0a269d7871223bd3f162a2896")
    }

    override fun onBackPressed() {
        if(videoPlayer.onBackPressed()){
            super.onBackPressed()
        }
    }
}