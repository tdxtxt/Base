package com.tdxtxt.base

import android.content.Intent
import com.tdxtxt.base.net.AppRepository
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.base.net.observer.BaseObserverNetapi
import com.tdxtxt.baselib.tools.ToastHelper
import com.tdxtxt.baselib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
        btn_next_1.setOnClickListener {
//            startActivityForResult(Intent(this, SocialTestActivity::class.java)){
//                ToastHelper.showToast("${it.resultCode}")
//            }
        }
        btn_next_2.setOnClickListener {
            AppRepository.queryArticleList(1)
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
            startActivity(Intent(this, VideoActivity::class.java))
        }
    }
}
