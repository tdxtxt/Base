package com.tdxtxt.video.utils

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
object PlayerConstant {

    const val VERITCAL = 0
    const val HORIZONTA_REVERSE = 1
    const val HORIZONTA_FORWARD = 2



//    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
//    annotation class ErrorType {
//        companion object {
//            //错误的链接
//            val TYPE_SOURCE = 1
//            //解析异常
//            val TYPE_PARSE = 2
//            //其他异常
//            val TYPE_UNEXPECTED = 3
//
//        }
//    }

    /**
     * 播放状态，主要是指播放器的各种状态
     * -1               播放错误
     * 0                播放初始状态，停止状态
     * 1                准备成功
     * 2                播放开始
     * 3                正在播放
     * 4                暂停播放
     * 5                播放完成
     * 6                正在缓冲
     *
     * 11               播放内容尺寸
     */
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class PlaylerState {
        companion object {
            var STATE_ERROR = -1
            var STATE_IDLE = 0
            var STATE_PREPARED = 1
            var STATE_START = 2
            var STATE_PLAYING = 3
            var STATE_PAUSED = 4
            var STATE_COMPLETED = 5
            var STATE_BUFFERING = 7

            var CHANGE_VIDEO_SIZE = 11
            var CHANGE_MULTIPLE = 12
        }
    }
}