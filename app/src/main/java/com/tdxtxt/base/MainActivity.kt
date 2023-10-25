package com.tdxtxt.base

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.TimeUtils
import com.tdxtxt.base.databinding.ActivityMainBinding
import com.tdxtxt.base.dialog.TestDialog
import com.tdxtxt.base.net.AppRepository
import com.tdxtxt.base.net.data.BaseResponse
import com.tdxtxt.base.net.observer.BaseObserverNetapi
import com.tdxtxt.baselib.image.ImageLoader
import com.tdxtxt.baselib.tools.ToastHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.pickerview.dataset.OptionDataSet
import com.tdxtxt.pickerview.util.PickerUtils
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), IViewBinding<ActivityMainBinding> {
    override fun view2Binding(rootView: View): ActivityMainBinding {
        return ActivityMainBinding.bind(rootView)
    }
    override fun getLayoutResId() = R.layout.activity_main

    override fun initUi() {
//        getStateView(R.id.iv_image).showLoading()
        ImageLoader.loadImageCircle(viewbinding().ivImage, "https://n.sinaimg.cn/tech/transform/346/w179h167/20220119/090c-d3cbde60cd5d0eac46025e8c740c9e90.gif", 2f, Color.BLUE)

//        ImageLoader.loadImageRoundRect(iv_image, "https://img95.699pic.com/photo/50136/1351.jpg_wh300.jpg", 12f)
        viewbinding().btnNext1.setOnClickListener {
//            PickerUtils.showDateTime(fragmentActivity, "选择时间",
//                TimeUtils.string2Millis("1900-01-06", "yyyy-MM-dd"),
//                TimeUtils.string2Millis("2024-10-06", "yyyy-MM-dd"),
//                TimeUtils.string2Millis("1910-03-06", "yyyy-MM-dd")) {
//
//            }
//            PickerUtils.showOneWheelStr(fragmentActivity, "", listOf("121", "21312", "2323")){
//                ToastHelper.showToast(it)
//            }
//            PickerUtils.showOneWheel(fragmentActivity, "xxx", mutableListOf(Dast("12"), Dast("34"), Dast("3fd"), Dast("3fd")), "34"){}

//            getStateView(R.id.iv_image).showEmpty("哈哈哈哈")
            startActivity(Intent(fragmentActivity, SocialTestActivity::class.java))
//            TestDialog(fragmentActivity).show()
        }
        viewbinding().btnNext2.setOnClickListener {
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
        viewbinding().btnNext3.setOnClickListener {
            startActivity(Intent(this, TXVideoActivity::class.java))
        }
        viewbinding().btnNext4.setOnClickListener {
            startActivity(Intent(this, TXLiveActivity::class.java))
        }
        viewbinding().btnNext5.setOnClickListener {
            startActivity(Intent(this, XTabLayoutActivity::class.java))
        }
    }

    class Dast(val content: String) : OptionDataSet {

        override fun getCharSequence() = content

        override fun getValue() = content

        override fun getSubs() = null
    }
}
