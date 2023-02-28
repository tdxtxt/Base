package com.tdxtxt.video.utils

import android.os.Handler
import android.os.Looper
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
}