package com.tdxtxt.social.qq.utils

import android.app.Activity
import android.os.Bundle
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction
import com.tdxtxt.social.core.platform.Target
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class QQShareHelper(val tencentApi: Tencent) : IShareAction, Recyclable, IUiListener {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null
    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        mListener = listener
        mComplete = complete
        super.share(activity, target, entity, listener)
    }

    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val params = Bundle()
        if(shareTarget == Target.SHARE_QQ_FRIENDS){

        }else if(shareTarget == Target.SHARE_QQ_ZONE){
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, entity?.content)
            tencentApi.publishToQzone(activity, params, this)
        }
    }

    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val params = Bundle()
        if(shareTarget == Target.SHARE_QQ_FRIENDS){
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, entity?.imagePath)
            tencentApi.shareToQQ(activity, params, this)
        }else if(shareTarget == Target.SHARE_QQ_ZONE){
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, entity?.imagePath)
            tencentApi.shareToQQ(activity, params, this)
        }
    }

    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val params = Bundle()
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "")
        params.putString(QQShare.SHARE_TO_QQ_TITLE, entity?.title)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity?.content)
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, entity?.webUrl)

        if(entity?.thumbUrl?.startsWith("http") == true){
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity?.thumbUrl)
        }else{
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, entity?.thumbPath)
        }

        if(shareTarget == Target.SHARE_QQ_FRIENDS){
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        }else if(shareTarget == Target.SHARE_QQ_ZONE){
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        }
        tencentApi.shareToQQ(activity, params, this)
    }

    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        //不支持
    }

    override fun onDestory() {
        mListener = null
        mComplete = null
    }

    override fun onComplete(p0: Any?) {
        mListener?.onSuccess()
        mComplete?.invoke()
    }

    override fun onError(error: UiError?) {
        mListener?.onFailure(error?.errorMessage)
        mComplete?.invoke()
    }

    override fun onCancel() {
        mListener?.onCancel()
        mComplete?.invoke()
    }

    override fun onWarning(p0: Int) {

    }
}