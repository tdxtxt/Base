package com.tdxtxt.social.core.bean

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.tdxtxt.social.core.R
import com.tdxtxt.social.core.platform.Target

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   :
 * </pre>
 */
class ShareEntity(val shareObjType: Int): Parcelable {
    //缩略图
    var thumbUrl: String? = null
    var thumbResId: Int? = null
    var thumbPath: String? = null

    //分享文字或web链接
    var title: String? = null
    var content: String? = null
    var webUrl: String? = null

    //分享图片相关
    var imageUrl: String? = null
    var imagePath: String? = null

    //分享小程序
    var miniProgramUserName: String? = null
    var miniProgramPath: String? = null

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        thumbUrl = parcel.readString()
        thumbResId = parcel.readInt()
        thumbPath = parcel.readString()
        title = parcel.readString()
        content = parcel.readString()
        webUrl = parcel.readString()
        imageUrl = parcel.readString()
        imagePath = parcel.readString()
        miniProgramUserName = parcel.readString()
        miniProgramPath = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(shareObjType)
        parcel.writeString(thumbUrl)
        parcel.writeInt(thumbResId ?: 0)
        parcel.writeString(thumbPath)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(webUrl)
        parcel.writeString(imageUrl)
        parcel.writeString(imagePath)
        parcel.writeString(miniProgramUserName)
        parcel.writeString(miniProgramPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShareEntity> {
        var DEFAULT_THUMB_RESID = R.mipmap.ic_share_thumb

        const val SHARE_TYPE_TEXT = 0x41   // 分享文字
        const val SHARE_TYPE_IMAGE = 0x42  // 分享图片
        const val SHARE_TYPE_WEB = 0x44    // 分享web
        const val SHARE_TYPE_MINIPROGRAM = 0x45 //分享小程序

        // 分享文字，qq 好友原本不支持，使用intent兼容
        @JvmStatic
        fun buildTextObj(title: String?, content: String?): ShareEntity {
            val shareMediaObj = ShareEntity(SHARE_TYPE_TEXT)
            shareMediaObj.title = title
            shareMediaObj.content = content
            return shareMediaObj
        }

        // 分享图片，带描述，qq微信好友会分为两条消息发送
        @JvmStatic
        fun buildImageObj(path: String?, content: String? = null): ShareEntity {
            val shareMediaObj = ShareEntity(SHARE_TYPE_IMAGE)
            if(path?.startsWith("http") == true) shareMediaObj.imageUrl = path else shareMediaObj.imagePath = path
            shareMediaObj.content = content
            return shareMediaObj
        }

        // 分享web，打开链接
        @JvmStatic
        fun buildWebObj(title: String?, content: String?, thumbPath: String?, webUrl: String?): ShareEntity {
            val shareMediaObj = ShareEntity(SHARE_TYPE_WEB)
            shareMediaObj.title = title
            shareMediaObj.content = content
            shareMediaObj.webUrl = webUrl
            if(thumbPath?.startsWith("http") == true) shareMediaObj.thumbUrl = thumbPath else shareMediaObj.thumbPath = thumbPath
            return shareMediaObj
        }

        // 分享小程序
        @JvmStatic
        fun buildMiniProgramObj(title: String?, content: String?, thumbPath: String?, webUrl: String?, miniProgramUserName: String?, miniProgramPath: String?): ShareEntity {
            val shareMediaObj = ShareEntity(SHARE_TYPE_MINIPROGRAM)
            shareMediaObj.title = title
            shareMediaObj.content = content
            shareMediaObj.webUrl = webUrl
            shareMediaObj.miniProgramPath = miniProgramPath
            shareMediaObj.miniProgramUserName = miniProgramUserName
            if(thumbPath?.startsWith("http") == true) shareMediaObj.thumbUrl = thumbPath else shareMediaObj.thumbPath = thumbPath
            return shareMediaObj
        }

        fun checkValid(@Target.ShareTarget shareTarget: Int, entity: ShareEntity?): Pair<Boolean, String?> {
            if(entity == null) return Pair(false, "entity == null")
            when (entity.shareObjType){
                SHARE_TYPE_TEXT -> {
                    if(TextUtils.isEmpty(entity.title) || TextUtils.isEmpty(entity.content)){
                        return Pair(false, "title == null or content == null")
                    }
                }
                SHARE_TYPE_WEB -> {
                    if(TextUtils.isEmpty(entity.title) || TextUtils.isEmpty(entity.content)){
                        return Pair(false, "title == null or content == null")
                    }
                    if(TextUtils.isEmpty(entity.webUrl)){
                        return Pair(false, "webUrl == null or title == null or content == null")
                    }
                }
                SHARE_TYPE_IMAGE -> {
                    if(TextUtils.isEmpty(entity.imagePath)){
                        return Pair(false, "imagePath == null")
                    }
                }
                SHARE_TYPE_MINIPROGRAM -> {
                    if(TextUtils.isEmpty(entity.title) || TextUtils.isEmpty(entity.content)){
                        return Pair(false, "title == null or content == null")
                    }
                    if(TextUtils.isEmpty(entity.miniProgramPath)){
                        return Pair(false, "miniprogramPath == null")
                    }
                }
            }
            return Pair(true, null)
        }

        override fun createFromParcel(parcel: Parcel): ShareEntity {
            return ShareEntity(parcel)
        }

        override fun newArray(size: Int): Array<ShareEntity?> {
            return arrayOfNulls(size)
        }
    }

}