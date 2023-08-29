package com.tdxtxt.base

import android.content.Intent
import android.util.Log
import android.view.View
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.tools.ToastHelper
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
        videoPlayer.setTitle("xxxxxxss")
        videoPlayer.setVideoManager(LiteAVManager.getVideoManage(1))
//        videoPlayer.setDataSource("https://mediaapi.juexiaotime.com/1111112023年法考资料/肖沛权/内部课/40diwuzhang.mp4", autoPlay = true)
//        videoPlayer.setDataSource("https://1307664769.vod2.myqcloud.com/83cdfc9bvodtranscq1307664769/6e52bafc243791576089935450/v.f100230.m3u8?t=646438e6&sign=444fe8ce44d379c54adda4670870ff8e")
//        videoPlayer.setWaterMark("糖的东东", 12, Color.RED)
        videoPlayer.bindLifecycle(this)
        videoPlayer.setMultipleList(mutableListOf(0.5f, 0.75f, 1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.75f, 2f))

        clickView(btn_1)
        clickView(btn_2)
        clickView(btn_3)
        clickView(btn_4)
        clickView(btn_5)
    }

    private fun clickView(view: View?){
        view?.setOnClickListener {
            when(it){
                btn_1 -> videoPlayer.setDataSource("https://1500013132.vod2.myqcloud.com/439520bavodtranscq1500013132/33f688d03270835009952696971/adp.1441797.m3u8", autoPlay = true)
                btn_2 -> videoPlayer.setDataSource("https://1307664769.vod2.myqcloud.com/83cdfc9bvodtranscq1307664769/fe365001243791579756943434/adp.1444425.m3u8?t=64d3359a&sign=2510318af9d392c4199cd09b29c0d2e4", autoPlay = true)
                btn_3 -> videoPlayer.setDataSource("https://1307664769.vod2.myqcloud.com/83cdfc9bvodtranscq1307664769/7d08e893387702302383374918/v.f100230.m3u8?t=64657c21&sign=7d7e70fa0d02ca7c943cac65be53ea2c", autoPlay = true)
                btn_4 -> videoPlayer.setDataSource("https://1307664769.vod2.myqcloud.com/83cdfc9bvodtranscq1307664769/1a317bd93270835010543269811/adp.1444425.m3u8?t=64c36d0e&sign=682a1c3727e9e9faf8d2155f4083cec6", autoPlay = true)
                btn_5 -> startActivity(Intent(fragmentActivity, TXLiveHTSActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if(videoPlayer.onBackPressed()){
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("tdxtxt", "被回收了")
        ToastHelper.showToast("被回收了")
    }
}