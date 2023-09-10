package com.tdxtxt.social.core.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tdxtxt.social.core.SocialGo
import com.tdxtxt.social.core.platform.IPlatform

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   : 登录分享承载体，用于接收登录和分享响应的结果
 * </pre>
 */
open class BaseActionActivity : Activity() {
    private var mIsNotFirstResume = false

    private fun getPlatform(): IPlatform? {
        val platform = SocialGo.getPlatform()
        return if (platform == null) {
            checkFinish()
            null
        } else
            platform
    }

    private fun checkFinish() {
        if (!isFinishing) {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    protected fun handleResp(resp: Any?) {
        getPlatform()?.onResponse(resp)
        checkFinish()
    }

    protected fun handleReq(req: Any?){
        getPlatform()?.onReq(this, req)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for wx & dd
        getPlatform()?.handleIntent(this)
        SocialGo.activeAction(this, intent.getIntExtra(KEY_ACTION_TYPE, -1))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        getPlatform()?.handleIntent(this)
    }

    override fun onResume() {
        super.onResume()
        if (mIsNotFirstResume) {
            getPlatform()?.handleIntent(this)
            // 留在目标 app 后在返回会再次 resume
            checkFinish()
        } else {
            mIsNotFirstResume = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPlatform()?.onActivityResult(requestCode, resultCode, data)
        checkFinish()
    }

    override fun onDestroy() {
        super.onDestroy()
        SocialGo.release(this)
    }



    companion object{
        const val ACTION_TYPE_LOGIN = 0
        const val ACTION_TYPE_SHARE = 1
        const val ACTION_TYPE_PAY = 2

        const val KEY_ACTION_TARGET = "KEY_ACTION_TARGET"
        const val KEY_ACTION_TYPE = "KEY_ACTION_TYPE"
        const val KEY_ACTION_PARMAS = "KEY_ACTION_PARMAS"
    }
}