package com.tdxtxt.liteavplayer.video.bean

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/14
 *     desc   :
 * </pre>
 */
class BitrateItem(val height: Int?, val index: Int?) {

    fun formatBitrate(): String{
        if(height == null) return ""
        if(height >= 1080) return "${height}p" // 超清
        if(height >= 720) return "${height}p" // 高清
        if(height >= 480) return "${height}p" // 清晰
        if(height >= 360) return "${height}p" // 流畅
        return "${height}p" // 省流
    }
}