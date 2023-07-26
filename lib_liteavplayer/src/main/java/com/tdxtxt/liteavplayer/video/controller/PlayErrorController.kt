package com.tdxtxt.liteavplayer.video.controller

import android.widget.TextView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.video.inter.AbsCustomController

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-07-26
 *     desc   :
 * </pre>
 */
class PlayErrorController(val errorMsg: String?) : AbsCustomController() {
    override fun getLayoutResId() = R.layout.liteavlib_view_control_error

    override fun onCreate() {
        findViewById<TextView>(R.id.tv_error)?.text = errorMsg
        findViewById<TextView>(R.id.btn_reload)?.setOnClickListener {
            getPlayerView()?.reStart(getPlayerView()?.getCurrentDuration())
        }
    }
}