package com.tdxtxt.baselib.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.tdxtxt.baselib.tools.CacheHelper

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/9
 *     desc   : 需在application中调用onCreate方法、attachBaseContext方法
 * </pre>
 */
abstract class ApplicationDelegate constructor(val app: Application) {

    abstract fun onPrivacyAfter(context: Context)
    abstract fun onPrivacyBefore(context: Context)

    fun attachBaseContext(base: Context?){
        MultiDex.install(base)
    }

    fun onCreate(){
        context = app
        delegateApp = this
        onPrivacyBefore(app)
        if(CacheHelper.isAgreePrivacy()){
            onPrivacyAfter(app)
        }
    }

    companion object{
        @JvmStatic
        var context: Context? = null
        @JvmStatic
        var delegateApp: ApplicationDelegate? = null
    }
}