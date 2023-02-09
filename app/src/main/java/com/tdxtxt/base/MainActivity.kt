package com.tdxtxt.base

import android.content.Intent
import com.baselib.helper.image.glide.GlideImageLoader
import com.tdxtxt.baselib.tools.RequestPermissionManager
import com.tdxtxt.baselib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
        btn_next_1.setOnClickListener {
            startActivity(Intent(this, SocialTestActivity::class.java))
        }
    }
}
