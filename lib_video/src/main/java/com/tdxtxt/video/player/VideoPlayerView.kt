package com.tdxtxt.video.player

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.tdxtxt.video.VideoPlayerManager
import com.tdxtxt.video.kernel.inter.IVideoPlayer
import com.tdxtxt.video.kernel.inter.VideoPlayerListener
import com.tdxtxt.video.player.view.BaiseControllerView
import com.tdxtxt.video.player.view.GestureController
import com.tdxtxt.video.player.view.IController
import com.tdxtxt.video.utils.PlayerConstant

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/17
 *     desc   :
 * </pre>
 */
class VideoPlayerView constructor(
    context: Context,
    val mManager: VideoPlayerManager,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr), IVideoPlayer by mManager.player,
    IVideoView, VideoPlayerListener {
    var baiseView: BaiseControllerView

    constructor(context: Context) : this(context, VideoPlayerManager.newInstance(), null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, VideoPlayerManager.newInstance(), attributeSet, 0)

    init{
        mManager.player.setPlayerEventListener(this)
        baiseView = BaiseControllerView(context).apply { attachPlayer(mManager.getVideoPlayer()) }
        addView(baiseView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        GestureController(baiseView).apply { attachPlayer(mManager.getVideoPlayer()) }
    }

    fun getVideoPlayerManager() = mManager
    fun getVideoPlayer() = mManager.getVideoPlayer()

    override fun showCustomView(view: View) {
        TODO("Not yet implemented")
    }

    override fun hideCustomView() {
        TODO("Not yet implemented")
    }

    override fun setCover(resId: Int) {
        baiseView.updateCover(resId)
    }

    override fun setRound(round: Float) {
        TODO("Not yet implemented")
    }

    override fun onPlayStateChanged(@PlayerConstant.PlaylerState state: Int, value: Any?) {
        when(state){
            PlayerConstant.PlaylerState.STATE_PREPARED -> {
                baiseView.updateTime(getVideoPlayer().getCurrentDuration(), getVideoPlayer().getDuration())
            }
            PlayerConstant.PlaylerState.STATE_START -> {
                baiseView.updatePauseUI()
            }
            PlayerConstant.PlaylerState.STATE_PAUSED -> {
                baiseView.updateStartUI()
            }
            PlayerConstant.PlaylerState.STATE_BUFFERING -> {
                baiseView.updateBufferProgress(if (value is Float) value else 1f)
            }
            PlayerConstant.PlaylerState.STATE_PLAYING -> {
                baiseView.changeProgress(getVideoPlayer().getCurrentDuration())
            }
            PlayerConstant.PlaylerState.CHANGE_VIDEO_SIZE -> {
                baiseView.changeVideoSize(getVideoPlayer().getVideoWidth(), getVideoPlayer().getVideoHeight())
            }
        }
    }
}