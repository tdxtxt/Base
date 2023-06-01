package com.tdxtxt.baselib.tools.timer

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/30
 *     desc   : 自定义倒计时控制器
 * </pre>
 */
class CountDownTimer {
    companion object {
        private val MSG = 500
        private val mHandler by lazy {
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    val listeners = mCountDownTime.listeners
                    if (listeners.isNotEmpty()) {
                        val iterator = listeners.keys.iterator()
                        while (iterator.hasNext()) {
                            val key = iterator.next()
                            val value = listeners.get(key)

                            if (value == null) {
                                iterator.remove()
                                listeners.remove(key)
                                break
                            }

                            if (value.countDownTimeSecond == 0) {
                                value.onTick(0)
                                value.onFinish()
                                iterator.remove()
                                listeners.remove(key)
                            } else {
                                value.onTick(abs(value.countDownTimeSecond))
                            }

                            value.countDownTimeSecond--
                        }
                    }

                    if (listeners.isNotEmpty()) {
                        sendMessageDelayed(obtainMessage(MSG), 1000)
                    }
                }
            }
        }

        private val mCountDownTime by lazy { CountDownTimer() }

        @JvmStatic
        fun getInstance() = mCountDownTime
    }

    private val listeners by lazy { ConcurrentHashMap<Int, TimerListener?>(2) }

    @Synchronized
    fun addTimerListener(listener: TimerListener?) {
        if (listener == null) return
        listeners[listener.key] = listener

        if (!mHandler.hasMessages(MSG)) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG))
        }
    }

    fun removeTimeListener(listener: TimerListener?) {
        if (listener == null) return
        synchronized(CountDownTimer::class.java) {
            listeners.remove(listener.key)

            if (listeners.isEmpty()) {
                mHandler.removeMessages(MSG)
            }
        }
    }

    fun removeTimeListener(listener: Any?) {
        if (listener is TimerListener) {
            removeTimeListener(listener)
        }
    }

}