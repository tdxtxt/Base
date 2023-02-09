package com.tdxtxt.baselib.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/9
 *     desc   : 需在application中调用onCreate方法、attachBaseContext方法
 * </pre>
 */
class ApplicationDelegate constructor(val app: Application) {
    fun attachBaseContext(base: Context?){
        MultiDex.install(base)
    }

    fun onCreate(){
        context = app
        delegateApp = this
    }

    companion object{
        @JvmStatic
        var context: Context? = null
        @JvmStatic
        var delegateApp: ApplicationDelegate? = null
    }
}