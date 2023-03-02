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
