package com.tdxtxt.social.core.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.tdxtxt.social.core.activity.BaseActionActivity
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.OnShareListener

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
abstract class AbsPlatform : IPlatform {
    override fun getActionClazz(): Class<*> {
        return BaseActionActivity::class.java
    }

    override fun isInstall(context: Context?): Boolean {
        return true
    }

    override fun login(activity: Activity?, listener: OnLoginListener?, complete: (() -> Unit)?) {

    }

    override fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {

    }


    override fun doPay(context: Context?, params: String?, listener: OnPayListener?, complete: (() -> Unit)?) {

    }

    override fun openMiniProgram(context: Context?, miniProgramId: String?, path: String?, isDebug: Boolean, complete: (() -> Unit)?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun handleIntent(activity: Activity) {

    }

    override fun onResponse(resp: Any?) {

    }

    override fun onReq(activity: Activity, req: Any?) {

    }

    override fun onDestory() {

    }


}