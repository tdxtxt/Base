package com.tdxtxt.video.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/24
 *     desc   :
 * </pre>
 */
class AutoSizeSurfaceView  : SurfaceView {
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoWidth = videoWidth
        mVideoHeight = videoHeight
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        var resultWidthSpecSize = widthSpecSize
        var resultHeightSpecSize = heightSpecSize

        if(mVideoWidth > 0 && mVideoHeight > 0){
            if (mVideoWidth * heightSpecSize < widthSpecSize * mVideoHeight) {
                resultWidthSpecSize = heightSpecSize * mVideoWidth / mVideoHeight
            } else if (mVideoWidth * heightSpecSize > widthSpecSize * mVideoHeight) {
                resultHeightSpecSize = widthSpecSize * mVideoHeight / mVideoWidth
            }
            /*val videoRatio = mVideoHeight.toFloat() / mVideoWidth.toFloat()

            if(widthSpecSize > 0 && heightSpecSize > 0){
                val viewRatio = heightSpecSize.toFloat() / widthSpecSize.toFloat()
                if(viewRatio > 1){//竖屏状态
                    if(videoRatio > viewRatio){
                        //如果视频比率> view比率 说明视频高度超高，以view高度为基准放缩 视频宽度
                        resultWidthSpecSize = (widthSpecSize * viewRatio / videoRatio).toInt()
                    }else{
                        resultHeightSpecSize = (heightSpecSize * videoRatio / viewRatio).toInt()
                    }
                }else{//横屏状态
                    if(videoRatio > viewRatio){
                        //如果视频比率> view比率 说明视频高度超高，以view宽度为基准放缩高度
                        resultWidthSpecSize = (widthSpecSize * viewRatio / videoRatio).toInt()
                    }else{
                        resultHeightSpecSize = (heightSpecSize * videoRatio / viewRatio).toInt()
                    }
                }
            }*/
        }
        setMeasuredDimension(resultWidthSpecSize, resultHeightSpecSize)
    }
}