package com.tdxtxt.social.core.platform

import android.app.Activity
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener

/**
 * @author Pinger
 * @since 2019/1/31 15:56
 * 分享类型
 */
interface IShareAction {
    fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?)

    fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?)

    fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?)

    fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?)

    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?) {
        if(entity == null) return
        when (entity.shareObjType) {
            ShareEntity.SHARE_TYPE_TEXT -> shareText(target, activity, entity)
            ShareEntity.SHARE_TYPE_IMAGE -> shareImage(target, activity, entity)
            ShareEntity.SHARE_TYPE_WEB -> shareWeb(target, activity, entity)
            ShareEntity.SHARE_TYPE_MINIPROGRAM -> shareMiniProgram(target, activity, entity)
        }
    }
}