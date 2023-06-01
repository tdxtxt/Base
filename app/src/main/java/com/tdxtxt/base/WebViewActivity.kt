package com.tdxtxt.base

import android.content.Intent
import com.tdxtxt.baselib.ui.CommToolBarActivity
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/30
 *     desc   :
 * </pre>
 */
class WebViewActivity : CommToolBarActivity() {
    override fun getLayoutResId() = R.layout.activity_webview

    override fun initUi() {
        webView.loadUrl("https://wj.qq.com/s2/11106170/b187/")
        webView.bindActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webView.onActivityResult(requestCode, resultCode, data)
    }
}