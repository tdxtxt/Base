package com.tdxtxt.baselib.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.multidex.MultiDex
import com.tdxtxt.baselib.tools.CacheHelper
import java.lang.ref.WeakReference

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
        initActivityLifecycleCallbacks()
        onPrivacyBefore(app)
        if(CacheHelper.isAgreePrivacy()){
            onPrivacyAfter(app)
        }
    }

    private fun initActivityLifecycleCallbacks(){
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                mAct.add(WeakReference(activity))
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                val iterator = mAct.iterator()
                while (iterator.hasNext()){
                    val actRef = iterator.next()
                    val act = actRef.get()
                    if(act == null || act.isFinishing || act.isDestroyed){
                        iterator.remove()
                    }else if(act == activity){
                        iterator.remove()
                        break
                    }
                }
            }
        })
    }

    companion object{
        @JvmStatic
        var context: Application? = null
        @JvmStatic
        var delegateApp: ApplicationDelegate? = null

        var mAct = mutableListOf<WeakReference<Activity?>>()

        fun getTopActivity(): Activity?{
            mAct.forEach {
                val act = it.get()
                if(act != null && !act.isFinishing && !act.isDestroyed){
                    return act
                }
            }
            return null
        }

        fun finishAllActivity(vararg keepActClass: Class<*>){
            val iterator = mAct.iterator()
            while (iterator.hasNext()){
                val actRef = iterator.next()
                val act = actRef.get()
                if(keepActClass.isEmpty()){
                    iterator.remove()
                    act?.finish()
                }else{
                    if(act == null || act.isFinishing || act.isDestroyed){
                        iterator.remove()
                        act?.finish()
                    }

                    keepActClass.forEach {
                        if(act?.javaClass?.name != it.name){
                            iterator.remove()
                            act?.finish()
                        }
                    }
                }
            }
        }

        fun finishActivity(vararg  finishClass: Class<*>){
            if(finishClass.isEmpty()) return

            val iterator = mAct.iterator()
            while (iterator.hasNext()){
                val actRef = iterator.next()
                val act = actRef.get()

                if(act == null || act.isFinishing || act.isDestroyed){
                    iterator.remove()
                    act?.finish()
                }

                finishClass.forEach {
                    if(act?.javaClass?.name == it.name){
                        iterator.remove()
                        act?.finish()
                    }
                }
            }
        }
    }
}