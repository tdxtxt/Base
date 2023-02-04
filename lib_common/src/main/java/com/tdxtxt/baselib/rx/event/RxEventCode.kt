package com.baselib.rx.event

abstract class RxEventCode constructor(val code: Int) {
    private var data: Any? = null

    fun setData(data: Any): RxEventCode{
        this.data = data
        return this
    }

    fun getData() = data

    override fun equals(other: Any?): Boolean {
        return if (other is RxEventCode) code == other.code else false
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    companion object{
        //登录成功通知
        val LOGIN = object : RxEventCode(1){}
        //申请登录通知
        val LOGIN_REQUEST = object : RxEventCode(2){}
        //登出通知
        val LOGOUT= object : RxEventCode(3){}
        //网络切换通知
        val NETWORK_CHANGE = object : RxEventCode(4){}
    }
}