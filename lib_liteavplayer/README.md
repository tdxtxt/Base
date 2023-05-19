
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