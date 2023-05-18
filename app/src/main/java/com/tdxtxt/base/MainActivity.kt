package com.tdxtxt.base

import android.content.Intent
import com.blankj.utilcode.util.TimeUtils
import com.tdxtxt.base.net.AppRepository
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.base.net.observer.BaseObserverNetapi
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.dialog.impl.CommMenuDialog
import com.tdxtxt.baselib.image.ImageLoader
import com.tdxtxt.baselib.tools.ToastHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.pickerview.dataset.OptionDataSet
import com.tdxtxt.pickerview.util.OnClickItemListener
import com.tdxtxt.pickerview.util.PickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
        getStateView(R.id.iv_image).showLoading()

//        ImageLoader.loadImageRoundRect(iv_image, "https://img2.baidu.com/it/u=3202947311,1179654885&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500", 12f)
        btn_next_1.setOnClickListener {
//            PickerUtils.showTime(fragmentActivity, "选择时间",
//                TimeUtils.string2Millis("2010-10-06", "yyyy-MM-dd"),
//                TimeUtils.string2Millis("2024-10-06", "yyyy-MM-dd"),
//                TimeUtils.string2Millis("2023-03-06", "yyyy-MM-dd")) {
//
//            }
//            PickerUtils.showOneWheel(fragmentActivity, "xxx", mutableListOf(Dast("12"), Dast("34"), Dast("3fd"), Dast("3fd")), "34"){}

//            getStateView(R.id.iv_image).showError(true)
            startActivity(Intent(fragmentActivity, LinkedViewActivity::class.java))
        }
        btn_next_2.setOnClickListener {
            AppRepository.queryArticleList(1)
                .delay(3000, TimeUnit.MILLISECONDS)
                .compose(bindUIThread())
                .compose(bindProgress())
                .subscribe(object : BaseObserverNetapi<Any>() {
                    override fun onSuccess(response: BaseResponse<Any>) {
                        ToastHelper.showToast(response.toString())
                    }
                    override fun onFailure(errorCode: Int?, errorMsg: String?, errorData: Any?) {

                    }

                })
        }
        btn_next_3.setOnClickListener {
            startActivity(Intent(this, TXVideoActivity::class.java))
        }
    }

    class Dast(val content: String) : OptionDataSet {

        override fun getCharSequence() = content

        override fun getValue() = content

        override fun getSubs() = null
    }
}
