package com.tdxtxt.base

import android.content.Intent
import android.view.View
import com.tdxtxt.base.databinding.ActivityWebviewBinding
import com.tdxtxt.baselib.ui.CommToolBarActivity
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/30
 *     desc   :
 * </pre>
 */
class WebViewActivity : CommToolBarActivity(), IViewBinding<ActivityWebviewBinding> {
    override fun getLayoutResId() = R.layout.activity_webview
    override fun viewbind(rootView: View): ActivityWebviewBinding {
        return ActivityWebviewBinding.bind(rootView)
    }
    override fun initUi() {
        viewbinding().webView.loadUrl("https://wj.qq.com/s2/11106170/b187/")
        viewbinding().webView.bindActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewbinding().webView.onActivityResult(requestCode, resultCode, data)
    }
}