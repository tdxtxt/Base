package com.tdxtxt.baselib.tools.timer

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/30
 *     desc   : 子类可继承，自行处理倒计时回调方法逻辑
 * </pre>
 */
abstract class TimerListener constructor(var countDownTimeSecond: Int, val key: Int) {
    /**
     * countDownTimeSecond 倒计时时间,小于0表示可以一直倒计
     */
    constructor(key: Int): this(-1, key)

    /**
     * second 剩余时间
     */
    abstract fun onTick(second: Int)
    /**
     * 倒计时结束
     */
    fun onFinish(){}

    /**
     * 设置倒计时时长，单位秒
     */
    fun setCountDownSecond(value: Int){
        if (value < 0) countDownTimeSecond = 0
        countDownTimeSecond = value
    }
}