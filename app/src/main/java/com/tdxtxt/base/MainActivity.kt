package com.tdxtxt.base

import com.baselib.helper.image.glide.GlideImageLoader
import com.tdxtxt.baselib.tools.RequestPermissionManager
import com.tdxtxt.baselib.ui.CommToolBarActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CommToolBarActivity() {

    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
        setTitleBar("表头")
        image.setOnClickListener {
            RequestPermissionManager.requestCameraPermission(this, "测算sdfdsfdsf出"){
                onDenied = {}
                onGranted = {}
            }
        }

        GlideImageLoader.loadImage(image, "https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF")
    }
}
