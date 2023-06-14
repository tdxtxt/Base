package com.tdxtxt.base

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

    override fun initUi() {
        livePlayer.setLiveManager(LiteAVManager.getLiveManage(applicationContext))

        livePlayer.setLiveSource("")
    }

}