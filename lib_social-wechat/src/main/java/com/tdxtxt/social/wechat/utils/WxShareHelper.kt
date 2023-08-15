package com.tdxtxt.social.wechat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction
import com.tdxtxt.social.core.platform.Target
import com.tdxtxt.social.core.utils.ThreadUtils
import com.tdxtxt.social.core.utils.Utils
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import java.io.File

/**
 * @author Pinger
 * @since 2019/1/31 17:53
 */
class WxShareHelper(private val wxApi: IWXAPI) : IShareAction, Recyclable {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null

    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?){
        mListener = listener
        mComplete = complete
        share(activity, target, entity, listener)
    }

    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val textObj = WXTextObject()
        textObj.text = entity?.content
        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        msg.title = entity?.title
        msg.description = entity?.content

        sendMsgToWx(msg, shareTarget, "txt")
    }

    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val localPath = entity?.imagePath
        if(localPath == null){
            mListener?.onFailure("分享图片地址不存在")
            return
        }
        ThreadUtils.getSinglePool().execute {
            val thumbData = Utils.getStaticSizeBitmapByteByPath(entity.imagePath)
            if(Utils.isGifFile(localPath)){
                val emoji = WXEmojiObject()
                emoji.emojiPath = localPath
                val msg = WXMediaMessage()
                msg.mediaObject = emoji
                msg.description = entity?.content
                msg.thumbData = thumbData
                sendMsgToWx(msg, shareTarget, "emoji")
            }else{
                // 文件大小不大于10485760  路径长度不大于10240
                val imgObj = WXImageObject()
                imgObj.imagePath = getFilePath(activity, localPath)
                val msg = WXMediaMessage()
                msg.mediaObject = imgObj
                msg.thumbData = thumbData
                sendMsgToWx(msg, shareTarget, "image")
            }
        }
    }

    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        ThreadUtils.getSinglePool().execute {
            val thumbData = Utils.getStaticSizeBitmapByteByPath(entity?.thumbPath)
            val webPage = WXWebpageObject()
            webPage.webpageUrl = entity?.webUrl
            val msg = WXMediaMessage(webPage)
            msg.title = entity?.title
            msg.description = entity?.content
            msg.thumbData = thumbData
            sendMsgToWx(msg, shareTarget, "web")
        }
    }

    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        ThreadUtils.getSinglePool().execute{
            val thumbData = Utils.getStaticSizeBitmapByteByPath(entity?.thumbPath)
            val miniProgram = WXMiniProgramObject()
            miniProgram.webpageUrl = entity?.webUrl
            miniProgram.miniprogramType =  WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE // 正式版:0，测试版:1，体验版:2
            miniProgram.userName = entity?.miniProgramUserName
            miniProgram.path = entity?.miniProgramPath
            val msg = WXMediaMessage(miniProgram)
            msg.title = entity?.title
            msg.description = entity?.content
            msg.thumbData = thumbData
            sendMsgToWx(msg, shareTarget, "miniProgram")
        }
    }

    override fun onDestory() {
        mListener = null
        mComplete = null
    }

    fun getListener() = mListener

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
    private fun getShareToWhere(target: Int): Int {
        var where = SendMessageToWX.Req.WXSceneSession
        when (target) {
            Target.SHARE_WX_FRIENDS -> where = SendMessageToWX.Req.WXSceneSession
            Target.SHARE_WX_ZONE -> where = SendMessageToWX.Req.WXSceneTimeline
        }
        return where
    }

    private fun sendMsgToWx(msg: WXMediaMessage, target: Int, sign: String) {
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction(sign)
        req.message = msg
        req.scene = getShareToWhere(target)
        val sendResult = wxApi.sendReq(req)
        if (!sendResult) {
            mListener?.onFailure("sendMsgToWx失败，可能是参数错误")
            mComplete?.invoke()
        }
    }

    private fun getFilePath(context: Context?, path: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context != null) {
            //要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(path))
            //授权给微信访问路径
            context.grantUriPermission("com.tencent.mm", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return uri.toString()
        }else{
            return path
        }
    }

}