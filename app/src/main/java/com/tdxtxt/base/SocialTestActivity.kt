package com.tdxtxt.base

import android.Manifest
import android.content.Intent
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.exception.SocialError
import com.pingerx.socialgo.core.model.ShareEntity
import com.pingerx.socialgo.core.platform.Target
import com.tdxtxt.baselib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_social_test.*

class SocialTestActivity : BaseActivity() {

    override fun getLayoutResId() = R.layout.activity_social_test

    override fun initUi() {
        initEvent()
        initView()
    }

    private var platformType: Int = Target.SHARE_QQ_FRIENDS
    private lateinit var shareMedia: ShareEntity

    private var mImageUrl = ShareEntity.THUMB_URL
    private var mShareUrl = "https://www.baidu.com/"


    private fun initView() {
        tvConsole.movementMethod = ScrollingMovementMethod.getInstance()
        rbTypeText.isChecked = true
        rbPlatformQQ.isChecked = true
    }

    private fun initEvent() {
        containerType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbTypeText -> {
                    shareMedia = ShareEntity.buildTextObj("我是文字分享的标题", "我是文字分享的内容")
                }
                R.id.rbTypeImage -> {
                    shareMedia = ShareEntity.buildImageObj(mImageUrl, "我是分享图片的描述")
                }
                R.id.rbTypeTextImage -> {
                    shareMedia = ShareEntity.buildImageObj(mImageUrl, "少年的时候，总是迫不及待地将自己的满腔爱意表达出来，而结果往往是陷入表演之中而不自知。所以两个人的记忆才会出现偏差，那些你觉得刻骨铭心的过去，对方往往没有同样的感觉，甚至茫然不知。")
                }

                R.id.rbTypeLink -> {
                    shareMedia = ShareEntity.buildWebObj("我是链接标题", "我是链接内容", mImageUrl, mShareUrl)
                }
            }
        }

        containerPlatform.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPlatformQQ -> platformType = Target.SHARE_QQ_FRIENDS
                R.id.rbPlatformQzon -> platformType = Target.SHARE_QQ_ZONE
                R.id.rbPlatformWx -> platformType = Target.SHARE_WX_ZONE
                R.id.rbPlatformWxFriend -> platformType = Target.SHARE_WX_FRIENDS
                R.id.rbPlatformSina -> platformType = Target.SHARE_WB
            }
        }
    }

    fun onQQLogin(view: View) {
        doLogin(Target.LOGIN_QQ)
    }

    fun onWxLogin(view: View) {
        doLogin(Target.LOGIN_WX)
    }

    fun onSinaLogin(view: View) {
        doLogin(Target.LOGIN_WB)
    }

    var positon = 1
    fun  onAliLogin(view: View){
        doLogin(Target.LOGIN_ALI, "ali login parmas posion = ${positon++}")
    }

    private fun doLogin(@Target.LoginTarget loginTarget: Int, parmas: String? = null) {
        SocialGo.doLogin(this, loginTarget, parmas) {
            onStart {
                showProgressBar()
                tvConsole?.text = "登录开始"
            }

            onSuccess {
                hideProgressBar()
                tvConsole?.text = it.socialUser?.toString()
            }

            onCancel {
                hideProgressBar()
                tvConsole?.text = "登录取消"
            }

            onFailure {
                hideProgressBar()
                tvConsole?.text = "登录异常 + ${it.errorMsg}"
            }
        }
    }

    fun onShare(view: View) {
        SocialGo.doShare(this, platformType, shareMedia) {
            onStart { _, _ ->
                showProgressBar()
                tvConsole?.text = "分享开始"
            }
            onSuccess {
                hideProgressBar()
                tvConsole?.text = "分享成功"
            }
            onFailure {
                hideProgressBar()
                tvConsole?.text = "分享失败${it.errorMsg}"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (it.errorCode == SocialError.CODE_STORAGE_READ_ERROR) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
                    } else if (it.errorCode == SocialError.CODE_STORAGE_WRITE_ERROR) {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                    }
                }
            }
            onCancel {
                hideProgressBar()
                tvConsole?.text = "分享取消"
            }
        }
    }

    fun onPayWx(view: View) {
        doPay(Target.PAY_WX)
    }

    fun onPayAli(view: View) {
        doPay(Target.PAY_ALI)
    }

    private fun doPay(@Target.PayTarget payTarget: Int) {
        SocialGo.doPay(this,
            "alipay_root_cert_sn=687b59193f3f462dd5336e5abf83c5d8_02941eef3187dddf3d3b83462e1dfcf6&alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_cert_sn=0af7d38b0738edc316be4883d671849f&app_id=2018081061044004&biz_content=%7B%22body%22%3A%22%7B%5C%22payChannel%5C%22%3A3%2C%5C%22payType%5C%22%3A1%7D%22%2C%22out_trade_no%22%3A%220010_2022012511052923%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%2222%E8%92%8B%E5%9B%9B%E9%87%91%E6%B0%91%E6%B3%95%E5%86%85%E9%83%A8%E5%B8%A6%E8%AF%BB%E3%80%90%E7%BA%B8%E8%B4%A8%E7%89%88%E3%80%91-%E5%AE%8C%E6%95%B4%E6%9D%83%E9%99%90%22%2C%22time_expire%22%3A%222022-01-25+11%3A40%3A29%22%2C%22total_amount%22%3A19.90%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay¬ify_url=https%3A%2F%2Fcenterapi.juexiaotime.com%2Fshopapi%2Fpay%2Fsuccess-notify&sign=E9jIRDcmklhW0xVqAKEs4IGu5hs1QjyBsZ7xtd6FsSyi%2F1lZJmemWLXqo7IgplzmTXZBR2WPKZ0HIfka4uDLJDLKWSfkaBvgQ9MZ%2B12Cyq778gs4t5GRO7n0ESJfPX6ARTwiP%2F6RqUXP%2BWkO7ayl%2BZxNdkt1vGTCX2KgQAzw2nG7ihyE%2BRWX8MRdUAgBpK7o7z%2Fqmnq2Qvhaljrsdv%2FvRimglcJWTvPUuM6JubsPK2muX1E0xj0KLYE0gHD%2FbufiWU3D2rsPihktdqVtH9zKrgo6ADQq1pL0%2BqL4sr%2Fn8K1I%2FlS6W5hW8iF1PJYMSFzZEwcYpTcoXhoM7KbEONfvfA%3D%3D&sign_type=RSA2×tamp=2022-01-25+11%3A08%3A45&version=1.0"
//                "{\"package\":\"Sign=WXPay\",\"appid\":\"wxcc6219445ce40bdf\",\"sign\":\"mifelI8zsiICgceeQkiuXJOhxeiZaaVzIul5LvYckihs0X2L3eDdpkPAedSQX28sRTdcfPtF4BM2E3RZIWiT9j\\/cd7GCrurOzvqk02VGkzmFO18RurkvRKcYuPDdsndh6Xcm2uWudiQhCI5OzeeFSOXuNHNv6BjxcWB7K0sDBqJhxJOLjeMarvhbfODxfWbui++j8WzBJIyv6j0g0MZC0hTToQl0eUKcwKast31FkCkOk9fYLWskWa1DnheW4QMztpNgm8UyP8wTZ78YdIPVwhzEOnpbrksLw9Z5Dtb8wpDU97j4BAew8XMpIMRBZLMfuPdUbq+NJJtPUm32+gIVww==\",\"partnerid\":\"1493232362\",\"prepayid\":\"wx26091630720578583cc5fb8ba088270000\",\"noncestr\":\"juexiaotime\",\"timestamp\":\"1637889689\"}"
//                "alipay_root_cert_sn=687b59193f3f462dd5336e5abf83c5d8_02941eef3187dddf3d3b83462e1dfcf6&alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_cert_sn=0af7d38b0738edc316be4883d671849f&app_id=2018081061044004&biz_content=%7B%22body%22%3A%22%7B%5C%22payChannel%5C%22%3A3%2C%5C%22payType%5C%22%3A1%7D%22%2C%22out_trade_no%22%3A%222021121714200700000080%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22LCY%E6%B5%8B%E8%AF%95%E5%86%85%E9%83%A8%E7%8F%AD0-%E5%AE%8C%E6%95%B4%E6%9D%83%E9%99%90%22%2C%22time_expire%22%3A%222021-12-17+14%3A55%3A07%22%2C%22total_amount%22%3A0.01%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fcenterdevapi.juexiaotime.com%2Fshopapi%2Fpay%2Fsuccess-notify&sign=hXavAg5G8GFkbVkinM9fLt%2FxYmUM1%2BGP4myTkyzEsBf0xfdUj0sn2wc2gPJ1CjYJB7TGqV2uk6%2B25QurycFyXC7Vb6o4Cj2QTm%2B2cW1od5lv1AGJNSj7%2FvfSaXNfqRongNbNtTOtqzmK9WSFxjohiH64HWnRcvhehyeEhi%2FfZkW4MGvLWZGDYx6%2F5%2FsKeZ7k1DJHdZ%2B1qSnaAMk0rjSU074NElZdPUMR7ZA8rp4eSoJaTBErg0SPeAxvSwAdkWRoIkDmZemb9hOFA9wn%2B%2Fc%2B392gWi0XXIOcLfC9f3asVhl3HXTlbJjFWsdTXRGG6rm%2F1M1209%2BcyRWaP4oWYr%2F6Sg%3D%3D&sign_type=RSA2&timestamp=2021-12-17+14%3A21%3A17&version=1.0"
            , payTarget) {

            onStart {
                tvConsole?.text = "支付开始"
            }

            onSuccess {
                tvConsole?.text = "支付成功"
            }

            onDealing {
                tvConsole?.text = "onDealing"
            }

            onFailure {
                tvConsole?.text = "支付异常：${it.errorMsg}"
            }

            onCancel {
                tvConsole?.text = "支付取消"
            }

            printLog {
                Log.i("xxxxxx", it?:"")
            }
        }
    }
}
