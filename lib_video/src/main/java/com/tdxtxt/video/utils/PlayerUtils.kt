package com.tdxtxt.video.utils

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/24
 *     desc   :
 * </pre>
 */
object PlayerUtils {
    private val mHandler = Handler(Looper.getMainLooper())

    fun postRunnable(runnable: Runnable, delayMillis: Long){
        mHandler.postDelayed(runnable, delayMillis)
    }

    fun removeRunnable(runnable: Runnable){
        mHandler.removeCallbacks(runnable)
    }

    fun formatTime(mills: Long): String{
        val seconds: Long = mills / 1000L
        val hours: Long = TimeUnit.SECONDS.toHours(seconds)
        val minutes: Long = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs: Long = TimeUnit.SECONDS.toSeconds(seconds) % 60

        val hoursStr = when (hours) {
            0L -> ""
            else -> "$hours:"
        }

        val minutesStr = String.format("%02d:", minutes.toInt())
        val secsStr = String.format("%02d", secs.toInt())
        return "${hoursStr}${minutesStr}${secsStr}"
    }

    fun formatMultiple(value: Float): String{
        val multe = (value * 100).toInt() % 10
        if(multe == 0){
            return String.format("%.1fX", value)
        }else{
            return String.format("%.2fX", value)
        }
    }

    fun hideSysBar(activity: Activity?) {
        activity?.window?.apply {
            var uiOptions = decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }

            decorView.systemUiVisibility = uiOptions
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun showSysBar(activity: Activity?) {
        activity?.window?.apply {
            var uiOptions = decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }

            decorView.systemUiVisibility = uiOptions
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}