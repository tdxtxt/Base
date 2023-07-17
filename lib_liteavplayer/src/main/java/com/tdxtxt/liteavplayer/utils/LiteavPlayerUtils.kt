package com.tdxtxt.liteavplayer.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/11
 *     desc   :
 * </pre>
 */
object LiteavPlayerUtils {
    private val mHandler = Handler(Looper.getMainLooper())

    fun postRunnable(runnable: Runnable, delayMillis: Long){
        mHandler.postDelayed(runnable, delayMillis)
    }

    fun removeRunnable(runnable: Runnable){
        mHandler.removeCallbacks(runnable)
    }

    fun formatMultiple(value: Float?): String{
        if(value == null) return "1X"
        val multe = (value * 100).toInt() % 10
        if(multe == 0){
            return String.format("%.1fX", value)
        }else{
            return String.format("%.2fX", value)
        }
    }

    fun formatTime(seconds: Int): String{
        val hours: Long = TimeUnit.SECONDS.toHours(seconds.toLong())
        val minutes: Long = TimeUnit.SECONDS.toMinutes(seconds.toLong()) % 60
        val secs: Long = TimeUnit.SECONDS.toSeconds(seconds.toLong()) % 60

        val hoursStr = when (hours) {
            0L -> ""
            else -> "$hours:"
        }

        val minutesStr = String.format("%02d:", minutes.toInt())
        val secsStr = String.format("%02d", secs.toInt())
        return "${hoursStr}${minutesStr}${secsStr}"
    }

    fun hideSysBar(activity: Activity?) {
        activity?.window?.apply {
            val uiOptions = decorView.systemUiVisibility

            decorView.systemUiVisibility = uiOptions or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun showSysBar(activity: Activity?) {
        activity?.window?.apply {
            val uiOptions = decorView.systemUiVisibility
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
//            }

            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


    fun getApplicationByReflect(): Application? {
        try {
            val activityThreadClass =
                Class.forName("android.app.ActivityThread")
            val thread: Any = getActivityThread() ?: return null
            val app = activityThreadClass.getMethod("getApplication").invoke(thread) ?: return null
            return app as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getActivityThread(): Any? {
        var activityThread = try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            activityThreadClass.getMethod("currentActivityThread").invoke(null)
        } catch (e: java.lang.Exception) {
            Log.e(
                "UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.message)
            null
        }
        if(activityThread == null){
            activityThread = try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val sCurrentActivityThreadField =
                    activityThreadClass.getDeclaredField("sCurrentActivityThread")
                sCurrentActivityThreadField.isAccessible = true
                sCurrentActivityThreadField[null]
            } catch (e: java.lang.Exception) {
                Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.message)
                null
            }
        }
        return activityThread
    }


    /**
     * 判断当前设备是否有网络连接
     */
    fun hasNetworkCapability(context: Context?): Boolean {
        try {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager?.activeNetwork ?: return false
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val networkInfo = connectivityManager?.activeNetworkInfo ?: return false
                return networkInfo.isAvailable && networkInfo.isConnected
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取当前网络状态
     */
    fun getNetworkState(context: Context?): NetworkState {
        try {
            if(context == null)  return NetworkState.NONE
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager?.activeNetwork ?: return NetworkState.NONE
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkState.NONE

                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.WIFI
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.MOBILE
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkState.ETHERNET
                    else -> NetworkState.NONE
                }
            } else {
                return when (connectivityManager?.activeNetworkInfo?.type) {
                    ConnectivityManager.TYPE_MOBILE -> NetworkState.MOBILE
                    ConnectivityManager.TYPE_WIFI -> NetworkState.WIFI
                    ConnectivityManager.TYPE_ETHERNET -> NetworkState.ETHERNET
                    else -> NetworkState.NONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return NetworkState.NONE
    }

    fun formatSpeed(byteSize: Int?): String {
        if(byteSize == null) return ""
        return if (byteSize <= 0) {
            return ""
        } else if (byteSize < 1024) {
            return "$byteSize kb/s"
        } else {
            return String.format("%.1fM/s", byteSize.toFloat() / 1024f)
        }
    }

    @JvmStatic
    fun getRandomNumber(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max) % (max - min + 1) + min
    }

    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getStatusBarHeight(context: Context?): Int {
        if(context == null) return 30
        val resources: Resources = context.getResources()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}
