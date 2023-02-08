package com.pingerx.socialgo.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import com.pingerx.socialgo.core.SocialGo
import com.pingerx.socialgo.core.common.SocialConstants
import com.pingerx.socialgo.core.exception.SocialError
import com.pingerx.socialgo.core.listener.OnShareListener
import com.pingerx.socialgo.core.model.ShareEntity
import com.pingerx.socialgo.core.platform.IShareAction
import com.pingerx.socialgo.core.platform.Target
import com.pingerx.socialgo.core.utils.SocialGoUtils
import com.pingerx.socialgo.core.utils.SocialLogUtils
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import java.io.File

/**
 * @author Pinger
 * @since 2019/1/31 17:53
 */
class WxShareHelper(private val wxApi: IWXAPI) : IShareAction {

    private var mListener: OnShareListener? = null

    override fun share(activity: Activity, target: Int, entity: ShareEntity, listener: OnShareListener) {
        mListener = listener
        super.share(activity, target, entity, listener)
    }

    override fun shareOpenApp(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        val rst = wxApi.openWXApp()
        if (rst) {
            onSuccess()
        } else {
            onFailure(SocialError(SocialError.CODE_CANNOT_OPEN_ERROR))
        }
    }

    override fun shareText(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        val textObj = WXTextObject()
        textObj.text = entity.getSummary()
        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        msg.title = entity.getTitle()
        msg.description = entity.getSummary()
        sendMsgToWx(msg, shareTarget, "text")
    }

    override fun shareImage(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        val app = activity.applicationContext
        SocialGo.getExecutor().execute {
            val thumbData = SocialGoUtils.getStaticSizeBitmapByteByPath(entity.getThumbImagePath())
            SocialGo.getHandler().post {
                if (thumbData == null) {
                    getThumbImageFailure("shareImage")
                } else {
                    shareImage(app, shareTarget, entity.getSummary(), entity.getThumbImagePath(), thumbData)
                }
            }
        }
    }

    override fun shareApp(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        SocialLogUtils.e("微信不支持app分享，将以web形式分享")
        shareWeb(shareTarget, activity, entity)
    }

    override fun shareWeb(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        SocialGo.getExecutor().execute {
            val thumbData = SocialGoUtils.getStaticSizeBitmapByteByPath(entity.getThumbImagePath())
            SocialGo.getHandler().post {
                if (thumbData == null) {
                    getThumbImageFailure("shareWeb")
                } else {
                    val webPage = WXWebpageObject()
                    webPage.webpageUrl = entity.getTargetUrl()
                    val msg = WXMediaMessage(webPage)
                    msg.title = entity.getTitle()
                    msg.description = entity.getSummary()
                    msg.thumbData = thumbData
                    sendMsgToWx(msg, shareTarget, "web")
                }
            }
        }
    }

    override fun shareMusic(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        SocialGo.getExecutor().execute {
            val thumbData = SocialGoUtils.getStaticSizeBitmapByteByPath(entity.getThumbImagePath())
            SocialGo.getHandler().post {
                if (thumbData == null) {
                    getThumbImageFailure("shareMusic")
                } else {
                    val music = WXMusicObject()
                    music.musicUrl = entity.getMediaPath()
                    val msg = WXMediaMessage()
                    msg.mediaObject = music
                    msg.title = entity.getTitle()
                    msg.description = entity.getSummary()
                    msg.thumbData = thumbData
                    sendMsgToWx(msg, shareTarget, "music")
                }
            }
        }
    }

    override fun shareVideo(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            when {
                SocialGoUtils.isHttpPath(entity.getMediaPath()) -> shareWeb(shareTarget, activity, entity)
                SocialGoUtils.isExist(entity.getMediaPath()) -> shareVideoByIntent(activity, entity, SocialConstants.WECHAT_PKG, SocialConstants.WX_FRIEND_PAGE)
                else -> onFailure(SocialError(SocialError.CODE_FILE_NOT_FOUND))
            }
        } else {
            SocialGo.getExecutor().execute {
                val thumbData = SocialGoUtils.getStaticSizeBitmapByteByPath(entity.getThumbImagePath())
                SocialGo.getHandler().post {
                    if (thumbData == null) {
                        getThumbImageFailure("shareVideo")
                    } else {
                        val video = WXVideoObject()
                        video.videoUrl = entity.getMediaPath()
                        val msg = WXMediaMessage(video)
                        msg.title = entity.getTitle()
                        msg.description = entity.getSummary()
                        msg.thumbData = thumbData
                        sendMsgToWx(msg, shareTarget, "video")
                    }
                }
            }
        }
    }

    override fun shareMiniProgram(shareTarget: Int, activity: Activity, entity: ShareEntity) {
        SocialGo.getExecutor().execute {
            val thumbData = SocialGoUtils.getStaticSizeBitmapByteByPath(entity.getThumbImagePath())
            SocialGo.getHandler().post {
                if (thumbData == null) {
                    getThumbImageFailure("shareMiniProgram")
                } else {
                    val miniProgram = WXMiniProgramObject()
                    miniProgram.webpageUrl = entity.getTargetUrl()
                    miniProgram.miniprogramType =  WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE // 正式版:0，测试版:1，体验版:2
                    miniProgram.userName = entity.getUserName()
                    miniProgram.path = entity.getPath()
                    val msg = WXMediaMessage(miniProgram)
                    msg.title = entity.getTitle()
                    msg.description = entity.getSummary()
                    msg.thumbData = thumbData
                    sendMsgToWx(msg, shareTarget, "miniProgram")
                }
            }
        }
    }

    private fun shareVideoByIntent(activity: Activity, obj: ShareEntity, pkg: String, page: String) {
        val result = SocialGoUtils.shareVideo(activity, obj.getMediaPath(), pkg, page)
        if (result) {
            onSuccess()
        } else {
            onFailure(SocialError(SocialError.CODE_SHARE_BY_INTENT_FAIL, "shareVideo by intent$pkg  $page failure"))
        }
    }


    private fun shareImage(context: Context, shareTarget: Int, desc: String, localPath: String, thumbData: ByteArray) {
        if (shareTarget == Target.SHARE_WX_FRIENDS) {
            if (SocialGoUtils.isGifFile(localPath)) {
                shareEmoji(shareTarget, localPath, desc, thumbData)
            } else {
                shareImage(context, shareTarget, localPath, thumbData)
            }
        } else {
            shareImage(context, shareTarget, localPath, thumbData)
        }
    }

    private fun shareImage(context: Context, shareTarget: Int, localPath: String, thumbData: ByteArray) {
        // 文件大小不大于10485760  路径长度不大于10240
        val imgObj = WXImageObject()
        imgObj.imagePath = getFilePath(context, localPath)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        msg.thumbData = thumbData
        sendMsgToWx(msg, shareTarget, "image")
    }

    private fun getFilePath(context: Context, path: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(path))
            //授权给微信访问路径
            context.grantUriPermission("com.tencent.mm", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return uri.toString()
        }else{
            return path
        }
    }

//    private String getFilePath(String path){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Uri contentUri = FileProvider.getUriForFile(this,
//            AppUtils.getAppPackageName() + ".fileprovider",  // 要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
//            new File(path));
//            // 授权给微信访问路径
//            grantUriPermission("com.tencent.mm",  // 这里填微信包名
//                    contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            String contentPath = contentUri.toString();
//
//            return contentPath;
//        }else {
//            return path;
//        }
//    }

    private fun shareEmoji(shareTarget: Int, localPath: String, desc: String, thumbData: ByteArray) {
        val emoji = WXEmojiObject()
        emoji.emojiPath = localPath
        val msg = WXMediaMessage()
        msg.mediaObject = emoji
        msg.description = desc
        msg.thumbData = thumbData
        sendMsgToWx(msg, shareTarget, "emoji")
    }


    private fun getShareToWhere(shareTarget: Int): Int {
        var where = SendMessageToWX.Req.WXSceneSession
        when (shareTarget) {
            Target.SHARE_WX_FRIENDS -> where = SendMessageToWX.Req.WXSceneSession
            Target.SHARE_WX_ZONE -> where = SendMessageToWX.Req.WXSceneTimeline
        }
        return where
    }

    private fun sendMsgToWx(msg: WXMediaMessage, shareTarget: Int, sign: String) {
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction(sign)
        req.message = msg
        req.scene = getShareToWhere(shareTarget)
        val sendResult = wxApi.sendReq(req)
        if (!sendResult) {
            onFailure(SocialError(SocialError.CODE_SDK_ERROR, "$#sendMsgToWx失败，可能是参数错误"))
        }
    }

    private fun getThumbImageFailure(msg: String) {
        SocialLogUtils.e("图片压缩失败 -> $msg")
        onFailure(SocialError(SocialError.CODE_IMAGE_COMPRESS_ERROR, msg))
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    fun onSuccess() {
        mListener?.getFunction()?.onSuccess?.invoke()
    }

    fun onCancel() {
        mListener?.getFunction()?.onCancel?.invoke()
    }

    fun onFailure(error: SocialError) {
        mListener?.getFunction()?.onFailure?.invoke(error)
    }

}