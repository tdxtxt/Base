package com.tdxtxt.base

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.pingerx.socialgo.ali.alipay.AliPlatform
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.SocialGoConfig
import com.pingerx.socialgo.core.adapter.impl.DefaultGsonAdapter
import com.pingerx.socialgo.core.adapter.impl.DefaultRequestAdapter
import com.pingerx.socialgo.qq.QQPlatform
import com.pingerx.socialgo.wechat.WxPlatform
import com.tdxtxt.base.net.AppNetProvider
import com.tdxtxt.baselib.app.ApplicationDelegate
import com.tdxtxt.baselib.tools.CacheHelper
import com.tdxtxt.baselib.tools.LogA
import com.tdxtxt.net.NetMgr


/**
 * @author Pinger
 * @since 18-7-20 下午4:30
 */

class DemoApplication : Application() {
    lateinit var mApplicationDelegate: ApplicationDelegate

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        mApplicationDelegate = ApplicationDelegateImpl(this)
        mApplicationDelegate.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        mApplicationDelegate.onCreate()
    }
}

class ApplicationDelegateImpl constructor(val context: Application) : ApplicationDelegate(context) {
    override fun onPrivacyAfter(context: Context) {
    }

    override fun onPrivacyBefore(context: Context) {
        CacheHelper.init(context)
        NetMgr.registerProvider(AppNetProvider())
        if(BuildConfig.DEBUG){
            Logger.addLogAdapter(AndroidLogAdapter())
            LogA.plant(object : LogA.DebugTree(){
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?){
                    Logger.log(priority, tag, message, t)
                }
            })
        }

        val config = SocialGoConfig.create(context)
            .debug(true)
            .qq(AppConstant.QQ_APP_ID)
            .wechat(AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET)
            .weibo(AppConstant.SINA_APP_KEY)
            .defImageResId(R.mipmap.ic_launcher_round)

        SocialGo
            .init(config)
            .registerWxPlatform(WxPlatform.Creator())
//                .registerWbPlatform(WbPlatform.Creator())
            .registerQQPlatform(QQPlatform.Creator())
            .registerAliPlatform(AliPlatform.Creator())
            .setJsonAdapter(DefaultGsonAdapter())
            .setRequestAdapter(DefaultRequestAdapter())
    }

}