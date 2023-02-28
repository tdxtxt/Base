package com.tdxtxt.base

import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.CommToolBarActivity
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
        videoPlayer.setDataSource("https://mediaapi.juexiaotime.com/1111112023年法考资料/肖沛权/内部课/40diwuzhang.mp4")
        videoPlayer.start()
    }
}