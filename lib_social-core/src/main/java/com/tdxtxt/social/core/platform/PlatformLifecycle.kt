package com.tdxtxt.social.core.platform

import android.app.Activity
import android.content.Intent
import com.tdxtxt.social.core.lisenter.Recyclable

/**
 * 各个平台执行的生命周期绑定
 */
interface PlatformLifecycle : Recyclable {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun handleIntent(activity: Activity)

    fun onResponse(resp: Any?)

    fun onReq(activity: Activity, req: Any?)
}