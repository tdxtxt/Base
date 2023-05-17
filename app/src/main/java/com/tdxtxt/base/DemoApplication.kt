package com.tdxtxt.base

import android.app.Application
import android.content.Context
import android.util.ArrayMap
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.pingerx.socialgo.ali.alipay.AliPlatform
import com.pingerx.socialgo.core.SocialSdk
import com.pingerx.socialgo.qq.QQPlatform
import com.pingerx.socialgo.wechat.WxPlatform
import com.tdxtxt.base.net.AppNetProvider
import com.tdxtxt.baselib.app.ApplicationDelegate
import com.tdxtxt.baselib.tools.CacheHelper
import com.tdxtxt.baselib.tools.LogA
import com.tdxtxt.liteavplayer.LiteAVManager
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

class ApplicationDelegateImpl constructor(val application: Application) : ApplicationDelegate(application) {
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

        SocialSdk.init(context, AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET, AppConstant.QQ_APP_ID)
            .registerWxPlatform(WxPlatform.Creator())
//            .registerWbPlatform(WbPlatform.Creator())
            .registerQQPlatform(QQPlatform.Creator())
            .registerAliPlatform(AliPlatform.Creator())

        LiteAVManager.init(application, "https://license.vod2.myqcloud.com/license/v2/1307664769_1/v_cube.license", "a784ace47a32b8bf0ba85bdac884e767", "https://1307664769.vod2.myqcloud.com")
    }

}