package com.tdxtxt.base

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.FileUtils
import com.tdxtxt.base.net.AppNetProvider
import com.tdxtxt.baselib.app.ApplicationDelegate
import com.tdxtxt.baselib.image.ImageLoader
import com.tdxtxt.baselib.tools.CacheHelper
import com.tdxtxt.liteavplayer.LiteAVManager
import com.tdxtxt.logger.LogA
import com.tdxtxt.logger.tree.DiskTree
import com.tdxtxt.net.NetMgr
import com.tdxtxt.social.alipay.AliPlatform
import com.tdxtxt.social.android.AndroidPlatform
import com.tdxtxt.social.core.SocialGo
import com.tdxtxt.social.core.lisenter.IRequestAdapter
import com.tdxtxt.social.wechat.WxPlatform
import java.io.File


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
//            Logger.addLogAdapter(AndroidLogAdapter())
//            LogA.plant(object : LogA.DebugTree(){
//                override fun log(priority: Int, tag: String?, message: String, t: Throwable?){
//                    Logger.log(priority, tag, message, t)
//                }
//            })
            LogA.plant(DiskTree(File(context.filesDir, "log").absolutePath))
        }
        SocialGo.init(SocialRequestAdapter())
        SocialGo.registerAndroidPlatform(AndroidPlatform.Creator())
        SocialGo.registerWxPlatform(WxPlatform.Creator(AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET))
        SocialGo.registerAliPlatform(AliPlatform.Creator())

//        SocialSdk.init(context, AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET, AppConstant.QQ_APP_ID)
//            .registerWxPlatform(WxPlatform.Creator())
////            .registerWbPlatform(WbPlatform.Creator())
//            .registerQQPlatform(QQPlatform.Creator())
//            .registerAliPlatform(AliPlatform.Creator())

//        LiteAVManager.init(application, "https://license.vod2.myqcloud.com/license/v2/1253499804_1/v_cube.license", "341ea3d21fe51789da3ad8c3ad47bd0f", "https://1307664769.vod2.myqcloud.com")
        LiteAVManager.init(application, "https://license.vod2.myqcloud.com/license/v2/1307664769_1/v_cube.license", "a784ace47a32b8bf0ba85bdac884e767", "edu.changan.com.cn")
    }
}

class SocialRequestAdapter : IRequestAdapter{
    override fun downloadImageSync(context: Context?, url: String?): File? {
        return ImageLoader.downloadImageSync(context, url)
    }
}