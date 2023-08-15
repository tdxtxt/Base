package com.tdxtxt.social.core.platform

import android.app.Activity
import android.content.Context
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.OnShareListener

/**
 * 定义第三方平台接口协议
 */
interface IPlatform : PlatformLifecycle {

    /**
     * 获取中间页的Clazz
     */
    fun getActionClazz(): Class<*>

    /**
     * 是否安装
     */
    fun isInstall(context: Context?): Boolean

    /**
     * 发起登录
     */
    fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?)

    /**
     * 发起分享
     */
    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?)

    /**
     * 支付
     */
    fun doPay(context: Context?, params: String?, listener: OnPayListener?, complete: (() -> Unit)?)
    /**
     * 打开小程序
     */
    fun openMiniProgram(context: Context?, miniProgramId: String?, path: String?, isDebug: Boolean, complete: (() -> Unit)?)
}
