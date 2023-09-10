package com.tdxtxt.social.dingtalk.utils

import android.app.Activity
import com.android.dingtalk.share.ddsharemodule.IDDShareApi
import com.android.dingtalk.share.ddsharemodule.message.*
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction
import com.tdxtxt.social.core.utils.SocialThreadUtils
import com.tdxtxt.social.core.utils.SocialUtils

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-15
 *     desc   :
 * </pre>
 */
class DingtalkShareHelper(private val dingtalkApi: IDDShareApi) : IShareAction, Recyclable {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null

    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?){
        mListener = listener
        mComplete = complete
        share(activity, target, entity, listener)
    }

    fun getListener() = mListener

    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val textMessage = DDTextMessage()
        textMessage.mText = entity?.content

        val mediaMessage = DDMediaMessage()
        mediaMessage.mMediaObject = textMessage

        val req = SendMessageToDD.Req()
        req.mMediaMessage = mediaMessage
        dingtalkApi.sendReq(req)
    }

    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val imageMessage = DDImageMessage()
        if(entity?.imageUrl?.isNotEmpty() == true){
            imageMessage.mImageUrl = entity.imageUrl
        }else{
            imageMessage.mImagePath = entity?.imagePath
        }

        val mediaMessage = DDMediaMessage()
        mediaMessage.mMediaObject = imageMessage

        val req = SendMessageToDD.Req()
        req.mMediaMessage = mediaMessage
        dingtalkApi.sendReq(req)
    }

    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        SocialThreadUtils.getSinglePool().execute {
            val webMessage = DDWebpageMessage()
            webMessage.mUrl = entity?.webUrl

            val thumbBitmap = SocialUtils.getStaticSizeBitmapByPath(entity?.thumbPath)

            val mediaMessage = DDMediaMessage()
            mediaMessage.mMediaObject = webMessage
            mediaMessage.mTitle = entity?.title
            mediaMessage.mContent = entity?.content
            mediaMessage.setThumbImage(thumbBitmap)

            val req = SendMessageToDD.Req()
            req.mMediaMessage = mediaMessage
            dingtalkApi.sendReq(req)
        }
    }

    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {

    }

    override fun onDestory() {
        mListener = null
        mComplete = null
    }
}