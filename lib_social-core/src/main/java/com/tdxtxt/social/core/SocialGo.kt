package com.tdxtxt.social.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.SparseArray
import com.tdxtxt.social.core.activity.BaseActionActivity
import com.tdxtxt.social.core.bean.ShareEntity
import com.tdxtxt.social.core.lisenter.IRequestAdapter
import com.tdxtxt.social.core.lisenter.OnLoginListener
import com.tdxtxt.social.core.lisenter.OnPayListener
import com.tdxtxt.social.core.lisenter.OnShareListener
import com.tdxtxt.social.core.platform.IPlatform
import com.tdxtxt.social.core.platform.PlatformCreator
import com.tdxtxt.social.core.platform.Target
import com.tdxtxt.social.core.utils.SocialFileUtils
import com.tdxtxt.social.core.utils.SocialThreadUtils


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-11
 *     desc   : 三方核心库
 * </pre>
 */
object SocialGo {
    private var mRequestAdapter: IRequestAdapter? = null
    private var mPlatformCreatorMap = SparseArray<PlatformCreator?>()
    private var mPlatform: IPlatform? = null
    ///////////////////////////////////////////////////////////////////////////
    // 初始化
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun init(requestAdapter: IRequestAdapter){
        mRequestAdapter = requestAdapter
    }
    ///////////////////////////////////////////////////////////////////////////
    // Platform 注册
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun registerWxPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_WX, creator)
    }
    @JvmStatic
    fun registerQQPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_QQ, creator)
    }
    @JvmStatic
    fun registerAliPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_ALI, creator)
    }
    @JvmStatic
    fun registerAndroidPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_ANDROID, creator)
    }
    @JvmStatic
    fun registerWxworkPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_WXWORK, creator)
    }
    @JvmStatic
    fun registerDingtalkPlatform(creator: PlatformCreator?){
        mPlatformCreatorMap.put(Target.PLATFORM_DINGTALK, creator)
    }
    ///////////////////////////////////////////////////////////////////////////
    // 登录SDK
    ///////////////////////////////////////////////////////////////////////////
    private var mLoginListener: OnLoginListener? = null
    @JvmStatic
    fun doLogin(context: Context?, @Target.LoginTarget loginTarget: Int, listenter: OnLoginListener?){
        if(context == null) return
        listenter?.onStart()
        val platformTarget = Target.mapPlatform(loginTarget)
        val platform = makePlatform(context, platformTarget)
        if(platform == null){
            listenter?.onFailure("未初始化平台")
        }else if(!platform.isInstall(context)){
            listenter?.onFailure("未安装应用")
        }else{
            mLoginListener = listenter
            val intent = Intent(context, platform.getActionClazz())
            intent.putExtra(BaseActionActivity.KEY_ACTION_TYPE, BaseActionActivity.ACTION_TYPE_LOGIN)
            intent.putExtra(BaseActionActivity.KEY_ACTION_TARGET, loginTarget)
            context?.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(0, 0)
            }
        }
    }
    /**
     * 跳转到中间页后，激活登录操作
     */
    private fun activeLogin(activity: Activity) {
        if(activity == null) return
        val intent = activity.intent
        val target = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TARGET, 0)
        val actionType = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TYPE, 0)

        getPlatform()?.login(activity, mLoginListener){
            release(activity)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 分享SDK
    ///////////////////////////////////////////////////////////////////////////
    private var mShareListener: OnShareListener? = null
    @JvmStatic
    fun doShare(context: Context?, @Target.ShareTarget shareTarget: Int, shareEntity: ShareEntity?, listenter: OnShareListener?){
        if(context == null) return
        if(shareEntity == null) return
        listenter?.onStart()
        SocialThreadUtils.getSinglePool().execute {
            prepareImageInBackground(context, shareEntity, listenter)
            SocialThreadUtils.runOnUiThread {
                startShare(context, shareTarget, shareEntity, listenter)
            }
        }
    }

    private fun startShare(context: Context?, @Target.ShareTarget shareTarget: Int, shareEntity: ShareEntity?, listenter: OnShareListener?){
        val result = ShareEntity.checkValid(shareTarget, shareEntity)
        if(result.first){//检测参数通过
            val platformTarget = Target.mapPlatform(shareTarget)
            val platform = makePlatform(context, platformTarget)
            if(platform == null){
                listenter?.onFailure("未初始化平台")
            }else if(!platform.isInstall(context)){
                listenter?.onFailure("未安装应用")
            }else{
                mShareListener = listenter
                val intent = Intent(context, platform.getActionClazz())
                intent.putExtra(BaseActionActivity.KEY_ACTION_TYPE, BaseActionActivity.ACTION_TYPE_SHARE)
                intent.putExtra(BaseActionActivity.KEY_ACTION_PARMAS, shareEntity)
                intent.putExtra(BaseActionActivity.KEY_ACTION_TARGET, shareTarget)
                context?.startActivity(intent)
                if (context is Activity) {
                    context.overridePendingTransition(0, 0)
                }
            }
        }else{
            listenter?.onFailure(result.second)
        }
    }

    /**
     * 下载图片
     */
    private fun prepareImageInBackground(context: Context?, shareEntity: ShareEntity?, listenter: OnShareListener?){
        if(shareEntity == null) return
        //缩略图下载
        if(shareEntity.thumbUrl?.startsWith("http") == true){//网络图片，需要下载
            val file = mRequestAdapter?.downloadImageSync(context, shareEntity.thumbUrl)
            shareEntity.thumbPath = file?.absolutePath
            listenter?.printLog("下载网络图片缩略图:${shareEntity.thumbUrl}---->${shareEntity.thumbPath}【size = ${file?.length()}】")
        }else {
            if(shareEntity.thumbResId == null && shareEntity.thumbResId != 0) shareEntity.thumbResId = ShareEntity.DEFAULT_THUMB_RESID
            shareEntity.thumbPath = SocialFileUtils.mapResId2LocalPath(context, shareEntity.thumbResId)
            listenter?.printLog("获取本地图片缩略图${shareEntity.thumbPath}")
        }
        //分享图下载
        if(shareEntity.imageUrl?.startsWith("http") == true){
            val file = mRequestAdapter?.downloadImageSync(context, shareEntity.imageUrl)
            shareEntity.imagePath = file?.absolutePath
            listenter?.printLog("下载网络图片分享图:${shareEntity.thumbUrl}---->${shareEntity.imageUrl}【size = ${file?.length()}】")
        }
    }

    /**
     * 跳转到中间页后，激活分享操作
     */
    private fun activeShare(activity: Activity?) {
        if(activity == null) return
        val intent = activity.intent
        val target = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TARGET, 0)
        val shareEntity = intent.getParcelableExtra<ShareEntity>(BaseActionActivity.KEY_ACTION_PARMAS)
        val actionType = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TYPE, 0)
        if(shareEntity == null){
            mShareListener?.onFailure("shareEntity == null")
        }else{
            getPlatform()?.share(activity, target, shareEntity, mShareListener){
                release(activity)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 支付SDK
    ///////////////////////////////////////////////////////////////////////////
    private var mPayListener: OnPayListener? = null
    fun doPay(context: Context?, @Target.PayTarget target: Int, params: String, listenter: OnPayListener?){
        if(context == null) return
        listenter?.onStart()
        val platformTarget = Target.mapPlatform(target)
        val platform = makePlatform(context, platformTarget)
        if(platform == null){
            listenter?.onFailure("未初始化平台")
        }else if(!platform.isInstall(context)){
            listenter?.onFailure("未安装应用")
        }else{
            mPayListener = listenter
            val intent = Intent(context, platform.getActionClazz())
            intent.putExtra(BaseActionActivity.KEY_ACTION_TARGET, target)
            intent.putExtra(BaseActionActivity.KEY_ACTION_PARMAS, params)
            intent.putExtra(BaseActionActivity.KEY_ACTION_TYPE, BaseActionActivity.ACTION_TYPE_PAY)
            context?.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(0, 0)
            }
        }
    }
    /**
     * 跳转到中间页后，激活分享操作
     */
    private fun activePay(activity: Activity) {
        if(activity == null) return
        val intent = activity.intent
        val target = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TARGET, 0)
        val parmas = intent.getStringExtra(BaseActionActivity.KEY_ACTION_PARMAS)
        val actionType = intent.getIntExtra(BaseActionActivity.KEY_ACTION_TYPE, 0)
        if(parmas == null){
            mPayListener?.onFailure("pay parmas == null")
        }else{
            getPlatform()?.doPay(activity, parmas, mPayListener){
                release(activity)
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // Platform相关操作
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun getPlatform(): IPlatform? {
        return mPlatform
    }

    private fun getPlatform(context: Context?, platformTarget: Int, parmas: String?): IPlatform? {
        val creator = mPlatformCreatorMap.get(platformTarget)
        return creator?.create(context, platformTarget, parmas)
    }

    private fun makePlatform(context: Context?, platformTarget: Int, parmas: String? = null): IPlatform? {
        val platform = getPlatform(context, platformTarget, parmas)
        mPlatform = platform
        return platform
    }

    fun activeAction(activity: Activity, actionType: Int) {
        if (actionType != -1) {
            when (actionType) {
                BaseActionActivity.ACTION_TYPE_LOGIN -> activeLogin(activity)
                BaseActionActivity.ACTION_TYPE_SHARE -> activeShare(activity)
                BaseActionActivity.ACTION_TYPE_PAY -> activePay(activity)
            }
        }
    }

    /**
     * 操作结束，关闭中间页，销毁静态变量
     */
    fun release(activity: Activity) {
        mPlatform?.onDestory()
        mPlatform = null
        mLoginListener = null
        mPayListener = null
        mShareListener = null
        SocialThreadUtils.cancel(SocialThreadUtils.getSinglePool())
        if (!activity.isFinishing) {
            activity.finish()
        }
    }
}