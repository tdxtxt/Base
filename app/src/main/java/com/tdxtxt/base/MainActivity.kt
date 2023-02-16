package com.tdxtxt.base

import android.content.Intent
import com.tdxtxt.base.net.AppRepository
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.base.net.observer.BaseObserverNetapi
import com.tdxtxt.base.net.observer.BaseObserverNetapi2
import com.tdxtxt.baselib.tools.ActivityExt.startActivityForResult
import com.tdxtxt.baselib.tools.ToastHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.net.model.AbsResponse
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
        btn_next_1.setOnClickListener {
            startActivityForResult(Intent(this, SocialTestActivity::class.java)){
                ToastHelper.showToast("${it.resultCode}")
            }
//            startActivity(Intent(this, SocialTestActivity::class.java))
        }
        btn_next_2.setOnClickListener {
            AppRepository.queryArticleList(1)
                .compose(bindUIThread())
                .compose(bindProgress())
                .subscribe(object : BaseObserverNetapi<Any>() {
                    override fun onSuccess(response: BaseResponse<Any>) {
                        ToastHelper.showToast(response.toString())
                    }

                    override fun onFailure(
                        response: BaseResponse<Any>?,
                        errorCode: Int?,
                        errorMsg: String?,
                        e: Throwable?
                    ) {
                        ToastHelper.showToast(errorMsg)
                    }
                })
        }
    }
}
