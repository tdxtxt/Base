package com.tdxtxt.video.kernel.impl.media

import android.content.Context
import com.tdxtxt.video.kernel.inter.PlayerFactory

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   : 不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 * </pre>
 */
class MediaPlayerFactory : PlayerFactory<AndroidMediaPlayer>() {
    override fun createPlayer(context: Context): AndroidMediaPlayer {
        return AndroidMediaPlayer(context.applicationContext)
    }
}