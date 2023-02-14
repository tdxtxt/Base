package com.pingerx.socialgo.core

import android.content.Context
import android.text.TextUtils
import com.pingerx.socialgo.core.listener.OnLoginJavaListener
import com.pingerx.socialgo.core.listener.OnLoginListener
import com.pingerx.socialgo.core.listener.OnPayJavaListener
import com.pingerx.socialgo.core.listener.OnPayListener
import com.pingerx.socialgo.core.model.ShareEntity
import com.pingerx.socialgo.core.platform.Target

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/14
 *     desc   :
 * </pre>
 */
object SocialSdk {
    fun init(context: Context, wxAppId: String? = null, wcSecretKey: String? = null, qqAppId: String? = null): SocialGo{
        val config = SocialGo.getConfig()?: SocialGoConfig.create(context)
        if(SocialGo.isInitSDK()){
            if(!TextUtils.isEmpty(wxAppId) && !TextUtils.isEmpty(wcSecretKey)){
                val oldWxAppId = SocialGo.getConfig()?.getWxAppId()
                if(wxAppId != oldWxAppId){
                    config.wechat(wxAppId!!, wcSecretKey!!)
                }
            }

            if(!TextUtils.isEmpty(qqAppId)){
                val oldAqAppId = SocialGo.getConfig()?.getQqAppId()
                if(qqAppId != oldAqAppId){
                    config.qq(qqAppId!!)
                }
            }
            return SocialGo.init(config)
        }

        if(!TextUtils.isEmpty(wxAppId) && !TextUtils.isEmpty(wcSecretKey)){
            config.wechat(wxAppId!!, wcSecretKey!!)
        }

        if(!TextUtils.isEmpty(qqAppId)){
            config.qq(qqAppId!!)
        }

        return SocialGo.init(config)
    }

    object SHARE{
        fun share(context: Context?, platformType: Int, shareBean: ShareEntity?){
            if (shareBean == null) return
            if (context == null) return

            SocialGo.doShare(context, platformType, shareBean){}
        }
        fun wechatFriendsWeb(context: Context, title: String, content: String, url: String, thumbImageUrl: String? = null){
            val bean = ShareEntity.buildWebObj(title, content, thumbImageUrl, url)
            share(context, Target.SHARE_WX_FRIENDS, bean)
        }

//        fun wechatFriendsWeb(context: Context, title: String, content: String, url: String, thumbImageResId: Int? = null){
//            val bean = ShareEntity.buildWebObj(title, content, null, url)
//            share(context, Target.SHARE_WX_FRIENDS, bean)
//        }

        fun wechatZoneWeb(context: Context, title: String, content: String, url: String, thumbImageUrl: String? = null){
            val bean = ShareEntity.buildWebObj(title, content, thumbImageUrl, url)
            share(context, Target.SHARE_WX_ZONE, bean)
        }

//        fun wechatZoneWeb(context: Context, title: String, content: String, url: String, thumbImageResId: Int? = null){
//            val bean = ShareEntity.buildWebObj(title, content, null, url)
//            share(context, Target.SHARE_WX_ZONE, bean)
//        }

        fun qqFriendsWeb(context: Context, title: String, content: String, url: String, thumbImageUrl: String? = null){
            val bean = ShareEntity.buildWebObj(title, content, thumbImageUrl, url)
            share(context, Target.SHARE_QQ_FRIENDS, bean)
        }

//        fun qqFriendsWeb(context: Context, title: String, content: String, url: String, thumbImageResId: Int? = null){
//            val bean = ShareEntity.buildWebObj(title, content, null, url)
//            share(context, Target.SHARE_QQ_FRIENDS, bean)
//        }

        fun qqZoneWeb(context: Context, title: String, content: String, url: String, thumbImageUrl: String? = null){
            val bean = ShareEntity.buildWebObj(title, content, thumbImageUrl, url)
            share(context, Target.SHARE_QQ_ZONE, bean)
        }

//        fun qqZoneWeb(context: Context, title: String, content: String, url: String, thumbImageResId: Int? = null){
//            val bean = ShareEntity.buildWebObj(title, content, null, url)
//            share(context, Target.SHARE_QQ_ZONE, bean)
//        }

        fun wechatFriendsImage(context: Context, path: String){
            val bean = ShareEntity.buildImageObj(path)
            share(context, Target.SHARE_WX_FRIENDS, bean)
        }

        fun wechatZoneImage(context: Context, path: String){
            val bean = ShareEntity.buildImageObj(path)
            share(context, Target.SHARE_WX_ZONE, bean)
        }

        fun qqFriendsImage(context: Context, path: String){
            val bean = ShareEntity.buildImageObj(path)
            share(context, Target.SHARE_QQ_FRIENDS, bean)
        }

        fun qqZoneImage(context: Context, path: String){
            val bean = ShareEntity.buildImageObj(path)
            share(context, Target.SHARE_QQ_ZONE, bean)
        }
    }

    object PAY{
        fun wechat(context: Context, payParmas: String, onFunction: OnPayListener.() -> Unit){
            SocialGo.doPay(context, payParmas, Target.PAY_WX, onFunction)
        }

        fun wechatJava(context: Context, payParmas: String, onFunction: OnPayJavaListener?){
            SocialGo.javaDoPay(context, payParmas, Target.PAY_WX, onFunction)
        }

        fun alipay(context: Context, payParmas: String, onFunction: OnPayListener.() -> Unit){
            SocialGo.doPay(context, payParmas, Target.PAY_ALI, onFunction)
        }

        fun alipayJava(context: Context, payParmas: String, onFunction: OnPayJavaListener?){
            SocialGo.javaDoPay(context, payParmas, Target.PAY_ALI, onFunction)
        }
    }

    object LOGIN{
        fun wechat(context: Context, onFunction: OnLoginListener.() -> Unit){
            SocialGo.doLogin(context, Target.LOGIN_WX, null, onFunction)
        }

        fun wechatJava(context: Context, payParmas: String, onFunction: OnLoginJavaListener){
            SocialGo.javaDoLogin(context, Target.LOGIN_WX, null, onFunction)
        }

        fun alipay(context: Context, payParmas: String, onFunction: OnLoginListener.() -> Unit){
            SocialGo.doLogin(context, Target.LOGIN_ALI, null, onFunction)
        }

        fun alipayJava(context: Context, payParmas: String, onFunction: OnLoginJavaListener){
            SocialGo.javaDoLogin(context, Target.LOGIN_ALI, null, onFunction)
        }
    }
}