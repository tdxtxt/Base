package com.tdxtxt.baselib.view.line

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.tdxtxt.baselib.R

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/21
 *     desc   : 虚线
 * </pre>
 */
class DashLineView: View {
    private val DEFAULT_DASH_WIDTH = SizeUtils.dp2px(1f).toFloat()
    private val DEFAULT_LINE_WIDTH = SizeUtils.dp2px(1f).toFloat()
    private val DEFAULT_LINE_COLOR = ContextCompat.getColor(context, R.color.gray_ebecf0)

    /**虚线的方向 */
    private val ORIENTATION_HORIZONTAL = 0
    private val ORIENTATION_VERTICAL = 1

    /**间距宽度 */
    private var dashWidth = DEFAULT_DASH_WIDTH

    /**线段宽度 */
    private var lineWidth = DEFAULT_LINE_WIDTH

    /**线段颜色 */
    private var lineColor = DEFAULT_LINE_COLOR
    private var dashOrientation = ORIENTATION_HORIZONTAL

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var widthSize = 0
    private var heightSize = 0

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DashLineView)
        dashOrientation = attributes.getInt(R.styleable.DashLineView_dlineOrientation, ORIENTATION_HORIZONTAL)
        lineWidth = attributes.getDimension(R.styleable.DashLineView_dlineWidth, DEFAULT_LINE_WIDTH)
        lineColor = attributes.getColor(R.styleable.DashLineView_dlineColor, DEFAULT_LINE_COLOR)
        dashWidth = attributes.getDimension(R.styleable.DashLineView_dlineSpace, DEFAULT_DASH_WIDTH)
        attributes.recycle()
        initView(context)
    }

    private fun initView(context: Context){
        mPaint.color = lineColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w - paddingLeft - paddingRight
        heightSize = h - paddingTop - paddingBottom
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
//        heightSize = MeasureSpec.getSize(heightMeasureSpec - paddingTop - paddingBottom)
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (dashOrientation) {
            ORIENTATION_VERTICAL -> drawVerticalLine(canvas)
            else -> drawHorizontalLine(canvas)
        }
    }

    /**
     * 画水平方向虚线
     * @param canvas
     */
    private fun drawHorizontalLine(canvas: Canvas) {
        var totalWidth = 0f
        canvas.save()
        mPaint.strokeWidth = height.toFloat()
        val pts = floatArrayOf(0f, 0f, lineWidth, 0f)
        //在画线之前需要先把画布向下平移办个线段高度的位置，目的就是为了防止线段只画出一半的高度
        //因为画线段的起点位置在线段左下角
        canvas.translate(0f,  height.toFloat() / 2f)
        while (totalWidth <= widthSize) {
            canvas.drawLines(pts, mPaint)
            canvas.translate(lineWidth + dashWidth, 0f)
            totalWidth += lineWidth + dashWidth
        }
        canvas.restore()
    }

    /**
     * 画竖直方向虚线
     * @param canvas
     */
    private fun drawVerticalLine(canvas: Canvas) {
        var totalWidth = 0f
        canvas.save()
        mPaint.strokeWidth = width.toFloat()
        val pts = floatArrayOf(0f, 0f, 0f, lineWidth)
        //在画线之前需要先把画布向右平移半个线段高度的位置，目的就是为了防止线段只画出一半的高度
        //因为画线段的起点位置在线段左下角
        canvas.translate(width.toFloat() / 2f, 0f)
        while (totalWidth <= heightSize) {
            canvas.drawLines(pts, mPaint)
            canvas.translate(0f, lineWidth + dashWidth)
            totalWidth += lineWidth + dashWidth
        }
        canvas.restore()
    }


}