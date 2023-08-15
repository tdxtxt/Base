package com.tdxtxt.social.wxwork.utils

import android.R
import android.app.Activity
import android.widget.EditText
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.lisenter.Recyclable
import com.tdxtxt.social.core.platform.IShareAction
import com.tencent.wework.api.IWWAPI
import com.tencent.wework.api.IWWAPIEventHandler
import com.tencent.wework.api.model.*


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class WxworkShareHelper(val wxworkApi: IWWAPI?) : IShareAction, IWWAPIEventHandler, Recyclable {
    private var mListener: OnShareListener? = null
    private var mComplete: (() -> Unit)? = null
    fun share(activity: Activity?, target: Int, entity: ShareEntity?, listener: OnShareListener?, complete: (() -> Unit)?) {
        mListener = listener
        mComplete = complete
        super.share(activity, target, entity, listener)
    }

    override fun shareText(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val txt = WWMediaText(entity?.content)
        txt.appPkg = activity?.packageName
        txt.appName = ""
//        txt.appId = APPID //企业唯一标识。创建企业后显示在，我的企业 CorpID字段
//        txt.agentId = AGENTID //应用唯一标识。显示在具体应用下的 AgentId字段
        wxworkApi?.sendMessage(txt, this)
    }

    override fun shareImage(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val img = WWMediaImage()
        img.fileName = entity?.imagePath?.hashCode()?.toString()
        img.filePath = entity?.imagePath
        img.appPkg = activity?.packageName
        img.appName = ""
//        img.appId = APPID; //企业唯一标识。创建企业后显示在，我的企业 CorpID字段
//        img.agentId = AGENTID;
        wxworkApi?.sendMessage(img, this)
    }

    override fun shareWeb(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {
        val link = WWMediaLink()
        link.thumbUrl = entity?.thumbUrl?:entity?.thumbPath
        link.webpageUrl = entity?.webUrl
        link.title = entity?.title
        link.description = entity?.content
        link.appPkg = activity?.packageName
        link.appName = ""
//        link.appId = APPID; //企业唯一标识。创建企业后显示在，我的企业 CorpID字段
//        link.agentId = AGENTID; //应用唯一标识。显示在具体应用下的 AgentId字段
        wxworkApi?.sendMessage(link, this)
    }

    override fun shareMiniProgram(shareTarget: Int, activity: Activity?, entity: ShareEntity?) {

    }

    override fun onDestory() {
        mListener = null
        mComplete = null
    }

    override fun handleResp(resp: BaseMessage?) {
        mComplete?.invoke()
    }
}