# 一、快速搭建项目

在搭建新项目中经常会用选择使用MVP架构搭建，同时使用网络请求、本地存储、弹框、分享、支付等常用功能。

### 功能
* 封装Base基类(Activity、Fragment)，一行代码实现titlebar、返回拦截
* 封装MVP架构项目
* 封装网络请求
* 封装图片加载
* 封装本地存储
* 封装日志打印
* 封装统一弹窗
* 封装常用控件：Toast、WebView、圆角容器、ViewState等


### 添加仓库依赖
```
maven { url "https://s01.oss.sonatype.org/content/groups/public" }
```
### 基础库CommonLib
```
implementation 'io.github.tdxtxt:baselib_beta:0.0.34-release'
```
### 网络库NetLib
```
implementation 'io.github.tdxtxt:net_beta:0.0.2-release'
```

# 二、三方分享、支付、登录的封装

### 功能
* 支持微信分享(小程序、朋友圈、好友)、支付、登录
* 支持支付宝支付、登录
* 支持QQ分享、登录
* 支持企业微信分享
* 支持钉钉分享（正在编码中...）
* 支持微博分享、登录（正在编码中...）

### 添加依赖（必须添加social-core）
```
maven { url "https://s01.oss.sonatype.org/content/groups/public" } //maven仓库地址

implementation 'io.github.tdxtxt:social-core_beta:0.0.1-release'//核心库
implementation 'io.github.tdxtxt:social-wechat_beta:0.0.1-release'//微信平台
implementation 'io.github.tdxtxt:social-alipay_beta:0.0.1-release'//阿里平台
```

### 使用流程
#### 初始化
```kotlin
SocialGo.init(SocialRequestAdapter())
SocialGo.registerWxPlatform(WxPlatform.Creator(AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET))
```
```kotlin
class SocialRequestAdapter : IRequestAdapter{
    override fun downloadImageSync(context: Context?, url: String?): File? {
        return ImageLoader.downloadImageSync(context, url)
    }
}
```
* app可见性配置【在manifest根节点进行配置即可】
```
<queries>
    <package android:name="com.tencent.mm" /> <!-- 指定微信包名 -->
    <package android:name="com.tencent.mobileqq" /> <!-- 指定QQ包名 -->
    <package android:name="com.sina.weibo" /> <!-- 指定微博包名 -->
    <package android:name="com.eg.android.AlipayGphone" /> <!-- 指定支付宝包名 -->
    <package android:name="hk.alipay.wallet" /> <!-- AlipayHK -->
</queries>
```
* 配置FileProvider
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <!--外部存储卡，谨慎使用： Environment.getExternalStorageDirectory() /storage/emulate/0-->
    <!--<external-path name="external_path" path="." />-->
    <!--根路径-->
    <!--<root-path name="root_path" path=""/>-->

    <!--Context.getCacheDir() /data/data/<包名>/cache-->
    <cache-path name="cache-path" path="." />
    <!--Context.getFilesDir() /data/data/<包名>/files-->
    <files-path name="files-path" path="." />
    <!--ContextCompat.getExternalMediaDirs()-->
    <external-media-path name="external_media_path" path="." />
    <!--ContextCompat.getExternalCacheDir() /storage/emulate/0/Android/data/<包名>/cache-->
    <external-cache-path name="external-cache-path" path="." />
    <!--ContextCompat.getExternalFilesDir(String) /storage/emulate/0/Android/data/<包名>/files-->
    <external-files-path name="external-files-path" path="." />
</paths>
```
```
<provider
     android:name="androidx.core.content.FileProvider"
     android:authorities="${applicationId}.fileprovider"
     android:exported="false"
     android:grantUriPermissions="true"
     tools:replace="android:authorities">
     <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/file_paths"
          tools:replace="android:resource" />
</provider>
```
#### 微信平台相关功能使用

* 微信sdk依赖
```
implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0'
```
* 回调activity配置

```
<activity
     android:name="{packageName}.WXEntryActivity"
     android:exported="true"
     android:launchMode="singleTask" />

<activity-alias
     android:name="${applicationId}.wxapi.WXPayEntryActivity"
	 android:exported="true"
     android:targetActivity="{packageName}.wxapi.WXEntryActivity" />
<activity-alias
     android:name="${applicationId}.wxapi.WXEntryActivity"
     android:exported="true"
     android:targetActivity="{packageName}.wxapi.WXEntryActivity" />
```
```kotiln
class WXEntryActivity : class WXEntryActivity : WxActionActivity()
```
* 分享
```kotlin
//shareTarget参数说明
//微信朋友圈:Target.SHARE_WX_ZONE
//微信好友:Target.SHARE_WX_FRIENDS

//shareBean参数说明
//分享文本:ShareEntity.buildTextObj()
//分享图片:ShareEntity.buildImageObj()
//分享web:ShareEntity.buildWebObj()
SocialGo.doShare(this, shareTarget, shareBean, object : OnShareListener(){
            override fun onSuccess() {
                ToastHelper.showToast("分享成功")
            }
            override fun onFailure(msg: String?) {
                ToastHelper.showToast("分享失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
* 登录
```kotlin
//loginTarget参数说明
//微信登录:Target.LOGIN_WX
SocialGo.doLogin(this, loginTarget, object : OnLoginListener(){
            override fun onSuccess(authCode: String?) {
                ToastHelper.showToast(authCode)
            }
            override fun onFailure(msg: String?) {
  				ToastHelper.showToast("登录失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
* 支付
```kotlin
//payTarget 参数说明
//支付宝支付:Target.PAY_WX
SocialGo.doPay(this, payTarget, "parmas", object: OnPayListener(){
            override fun onSuccess(auth: AuthInfo?) {
                ToastHelper.showToast(auth)
            }
            override fun onFailure(msg: String?) {
  				ToastHelper.showToast("登录失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
#### 阿里平台相关功能使用

* 阿里sdk依赖
```
implementation files('libs/alipaySdk-15.8.05.aar')
```
* 登录
```kotlin
//loginTarget参数说明
//支付宝登录:Target.LOGIN_ALI
SocialGo.doLogin(this, loginTarget, object : OnLoginListener(){
            override fun onSuccess(authCode: String?) {
                ToastHelper.showToast(authCode)
            }
            override fun onFailure(msg: String?) {
  				ToastHelper.showToast("登录失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
* 支付
```kotlin
//payTarget 参数说明
//支付宝支付:Target.PAY_ALI
SocialGo.doPay(this, payTarget, "parmas", object: OnPayListener(){
            override fun onSuccess(auth: AuthInfo?) {
                ToastHelper.showToast(auth)
            }
            override fun onFailure(msg: String?) {
  				ToastHelper.showToast("登录失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
#### QQ平台相关功能使用

* QQ依赖
```
implementation files('libs/open_sdk_r6020_lite.jar')
```

* 分享
```kotlin
//shareTarget参数说明
//微信朋友圈:Target.SHARE_QQ_ZONE
//微信好友:Target.SHARE_QQ_FRIENDS

//shareBean参数说明
//分享文本:ShareEntity.buildTextObj()
//分享图片:ShareEntity.buildImageObj()
//分享web:ShareEntity.buildWebObj()
SocialGo.doShare(this, shareTarget, shareBean, object : OnShareListener(){
            override fun onSuccess() {
                ToastHelper.showToast("分享成功")
            }
            override fun onFailure(msg: String?) {
                ToastHelper.showToast("分享失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
#### 微博平台相关功能使用

* 微博依赖
```
```
* 分享
```kotlin

```
* 登录
```kotlin

```
#### 企业微信平台相关功能使用
* 企业微信依赖
```
implementation files('libs/lib_wwapi-2.0.12.11.aar')
```
* 分享
```kotlin
//shareTarget参数说明
//微信朋友圈:Target.SHARE_QQ_ZONE
//微信好友:Target.SHARE_QQ_FRIENDS

//shareBean参数说明
//分享文本:ShareEntity.buildTextObj()
//分享图片:ShareEntity.buildImageObj()
//分享web:ShareEntity.buildWebObj()
SocialGo.doShare(this, shareTarget, shareBean, object : OnShareListener(){
            override fun onSuccess() {
                ToastHelper.showToast("分享成功")
            }
            override fun onFailure(msg: String?) {
                ToastHelper.showToast("分享失败: $msg")
            }
            override fun printLog(log: String?) {
                Log.i("tdxtxt===", log?: "")
            }
        })
```
#### 钉钉平台相关功能使用

* 钉钉依赖
```
```
* 分享
```kotlin

```

# 三、腾讯直播、点播 播放器的封装

### 功能
* 音视频播放
* 后台播放
* 变速播放
* 水印
* 直播

### 添加依赖
```
maven { url "https://s01.oss.sonatype.org/content/groups/public" } //maven仓库地址

implementation 'io.github.tdxtxt:liteavplayer_beta:0.0.5-release' //引入依赖
```

### 点播使用流程
* 第一步:初始化

```kotlin
LiteAVManager.init(application,"licenceURL","licenceKey", "防盗链接域名，可不传")
```

* 第二步:布局中添加播放器控件
```xml
<com.tdxtxt.liteavplayer.video.TXVideoPlayerView
        android:id="@+id/videoPlayer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:txWidthRatio="16"
        app:txHeightRatio="9"/>
```
* 第三步:界面中设置VideoManage对象
```kotlin
videoPlayer.setVideoManager(LiteAVManager.getVideoManage())
```
* 第四步:播放设置
```kotlin
//设置播放URL
videoPlayer.setDataSource("url", 开始播放时间int, 是否马上播放boolean)
//重写返回事件
 override fun onBackPressed() {
     if(videoPlayer.onBackPressed()){
         super.onBackPressed()
     }
}
```
* 其他配置说明
```kotlin
 videoPlayer.bindLifecycle(owner) //绑定生命周期
 videoPlayer.setMultipleList(mutableListOf(0.75f, 1f, 1.5f, 2f)) //设置倍速
 videoPlayer.setWaterMark("水印", 14, Color.argb(100, 255, 0, 0)) //设置水印
 videoPlayer.setTrackMaxPercent(0.5f) //可拖动的最大时长百分段，取值0到1
 videoPlayer.addPlayerEventListener(this) //设置播放状态监听，具体状态可查看源码
 videoPlayer.showCustomView() //设置自定义View，通常用来做广告、播放完成后的封面等，注意：这里只会设置一个，再次设置的时候会覆盖前面设置的额
 videoPlayer.hideCustomView() //移除自定义view
```

### 第三方底层SDK版本
* 腾讯直播SDK：https://cloud.tencent.com/document/product/881/81205

```