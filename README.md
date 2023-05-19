# 快速搭建项目基础工具Base封装

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
* 支持微信分享、登录、支付功能。
* 支持支付宝登录、支付功能。
* 支持QQ分享、登录功能。


### 添加仓库依赖
      maven { url "https://s01.oss.sonatype.org/content/groups/public" }

#### 基础库CommonLib

      implementation 'io.github.tdxtxt:baselib_beta:0.0.6-release'

#### 网络库NetLib

      implementation 'io.github.tdxtxt:net_beta:0.0.2-release'

#### 视频组件库VideoLib

     implementation 'io.github.tdxtxt:video_beta:0.0.1-release'

#### 三方分享、支付、登录库Social
* 平台核心库SDK（使用单个平台时必须添加核心库SDK）

      implementation 'io.github.tdxtxt:socialCore_beta:0.0.2-release'

* QQ平台SDK

      implementation 'io.github.tdxtxt:socialQQ_beta:0.0.1-release'

* 微信平台SDK

      implementation 'io.github.tdxtxt:socialWechat_beta:0.0.1-release'

* 微博平台SDK

      待验证...

* 支付宝平台SDK

      implementation 'io.github.tdxtxt:socialAlipay_beta:0.0.1-release'


### 使用流程
* 在Application中初始化第三方平台和配置各自的appkey

        SocialSdk.init(context, AppConstant.WX_APP_ID, AppConstant.WX_APP_SECRET, AppConstant.QQ_APP_ID)
            .registerWxPlatform(WxPlatform.Creator())
            .registerWbPlatform(WbPlatform.Creator())
            .registerQQPlatform(QQPlatform.Creator())
            .registerAliPlatform(AliPlatform.Creator())

* 登录

        SocialSdk.LOGIN.wechat(this){
                    onSuccess {  }
                    onFailure {  }
                }

* 分享

         SocialSdk.SHARE.wechatFriendsWeb(this, "title", "content", "https://baidu.com")


* 支付

         SocialSdk.PAY.wechat(this, ""){
                     onSuccess {  }
                     onFailure {  }
                 }


### 第三方底层SDK版本
* QQ：`open_sdk_r6020_lite.jar`
* 微信：`com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0`
* 支付宝：`alipaysdk-15.8.05.aar`




# 腾讯直播、点播 播放器的封装

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
LiteAVManager.init(application,"licenceURL","a784ace47a32b8bf0ba85bdac884e767", "防盗链接域名，可不传")
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