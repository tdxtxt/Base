package com.tdxtxt.social.android.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction
import java.io.File

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-15
 *     desc   :
 * </pre>
 */
class AndroidShareHelper: IShareAction, Recyclable {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null
    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        mListener = listener
        mComplete = complete
        super.share(activity, target, entity, listener)
    }
    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        if(activity == null) return

        val sendIntent = ShareCompat.IntentBuilder(activity)
            .setType("text/*") //包括 text/plain、text/rtf,、text/html、text/json
            .setText(entity?.content?:"")
            .setChooserTitle(entity?.title?:"")
            .createChooserIntent()
        startActivity(activity, sendIntent)
        mListener?.onSuccess()
        mComplete?.invoke()
    }
    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        if(activity == null) return

        val sendIntent = ShareCompat.IntentBuilder(activity)
            .setType("image/*") ///包括 image/jpg、image/png、image/gif
            .setStream(getFileUri(activity, entity?.imagePath))
            .setText(entity?.content?:"")
            .setChooserTitle(entity?.title?:"")
            .createChooserIntent()
        startActivity(activity, sendIntent)
        mListener?.onSuccess()
        mComplete?.invoke()

    }
    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        if(activity == null) return

        val sendIntent = ShareCompat.IntentBuilder(activity)
            .setType("text/*") //包括 text/plain、text/rtf,、text/html、text/json
            .setText("${entity?.title}${System.lineSeparator()}${entity?.content}${System.lineSeparator()}${entity?.webUrl}")
            .setChooserTitle(entity?.title?:"")
            .createChooserIntent()
        startActivity(activity, sendIntent)
        mListener?.onSuccess()
        mComplete?.invoke()
    }
    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {

    }

    override fun onDestory() {
        mListener = null
        mComplete = null
    }

    private fun startActivity(activity: Activity?, chooserIntent: Intent){
        if(activity == null) return
        if (chooserIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(chooserIntent)
        }
    }

    private fun getFileUri(context: Context?, path: String?): Uri? {
        if(path == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && context != null) {
            //要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(path))
            //授权给微信访问路径
            context.grantUriPermission("com.tencent.mm", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return uri
        }else{
            return  Uri.fromFile(File(path))
        }
    }
}