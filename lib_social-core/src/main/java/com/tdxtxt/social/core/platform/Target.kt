package com.tdxtxt.social.core.platform

import androidx.annotation.IntDef

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
object Target {
    const val PLATFORM_ANDROID = 0x10 //系统
    const val PLATFORM_QQ = 0x11 // qq
    const val PLATFORM_WX = 0x12 // 微信
    const val PLATFORM_ALI = 0x13 // 支付宝
    const val PLATFORM_WXWORK = 0x14 // 企业微信
    const val PLATFORM_DINGTALK = 0x15 //钉钉

    const val LOGIN_WX = 0x22 // 微信登录
    const val LOGIN_ALI = 0x24 // 阿里登录

    const val SHARE_QQ_FRIENDS = 0x31 // qq好友
    const val SHARE_QQ_ZONE = 0x32 // qq空间
    const val SHARE_WX_FRIENDS = 0x33 // 微信好友
    const val SHARE_WX_ZONE = 0x34 // 微信朋友圈
    const val SHARE_WXWORK_FRIENDS = 0x35 //企业微信好友
    const val SHARE_DINGTALK_FRIENDS = 0x36 //钉钉好友
    const val SHARE_ANDROID = 0x37 //系统分享

    const val PAY_WX = 0x41   //微信支付
    const val PAY_ALI = 0x42  // 阿里支付

    /**
     * 分享类型
     */
    @IntDef(SHARE_ANDROID, SHARE_QQ_FRIENDS, SHARE_QQ_ZONE, SHARE_WX_FRIENDS, SHARE_WX_ZONE, SHARE_WXWORK_FRIENDS, SHARE_DINGTALK_FRIENDS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ShareTarget


    /**
     * 登录类型
     */
    @IntDef(LOGIN_WX, LOGIN_ALI)
    @Retention(AnnotationRetention.SOURCE)
    annotation class LoginTarget

    /**
     * 支付类型
     */
    @IntDef(PAY_WX, PAY_ALI)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PayTarget

    fun mapPlatform(target: Int): Int {
        return when (target) {
            PLATFORM_QQ, SHARE_QQ_FRIENDS, SHARE_QQ_ZONE -> PLATFORM_QQ
            PLATFORM_WX, LOGIN_WX, SHARE_WX_FRIENDS, SHARE_WX_ZONE, PAY_WX -> PLATFORM_WX
            LOGIN_ALI, PAY_ALI, PLATFORM_ALI -> PLATFORM_ALI
            SHARE_WXWORK_FRIENDS -> PLATFORM_WXWORK
            SHARE_DINGTALK_FRIENDS -> PLATFORM_DINGTALK
            SHARE_ANDROID -> PLATFORM_ANDROID
            else -> -1
        }
    }

}