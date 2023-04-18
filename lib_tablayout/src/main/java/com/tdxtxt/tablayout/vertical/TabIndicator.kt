package com.tdxtxt.tablayout.vertical

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.tdxtxt.tablayout.tools.TabUtils

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/18
 *     desc   :
 * </pre>
 */
class TabIndicator : View {
    private val mLineRect = RectF()
    private var mRoundRadius = 0f
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mColor = Color.BLUE

    constructor(context: Context): super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView()
    }

    private fun initView(){
        mPaint.style = Paint.Style.FILL
        mRoundRadius = TabUtils.dp2px(2)
        mPaint.color = mColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mLineRect.left = 0f
        mLineRect.right = w.toFloat()
        mLineRect.top = 0f
        mLineRect.bottom = h.toFloat()
    }

    fun setRadius(roundRadius: Float){
        mRoundRadius = roundRadius
        invalidate()
    }

    fun setColor(color: Int){
        mPaint.color = color
        invalidate()
    }

    fun setWindowSize(width: Int, height: Int){
        layoutParams = layoutParams.apply {
            this.width = width
            this.height = height
        }
    }

    fun setParmas(width: Float, height: Float, color: Int, roundRadius: Float){
        mRoundRadius = roundRadius
        mPaint.color = color
        layoutParams = layoutParams.apply {
            this.width = width.toInt()
            this.height = height.toInt()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, mPaint)
    }

    override fun setBackgroundColor(color: Int) {
        setColor(color)
    }

}