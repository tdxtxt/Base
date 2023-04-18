package com.tdxtxt.baselib.view.textview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.view.edit.CustomTextWatcher

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/23
 *     desc   :
 * </pre>
 */
class RoundStyleTextView : AppCompatTextView{
    private var rtvBorderWidth = 0
    private var rtvUseEnableAlpha = true
    private var rtvBorderColor = Color.BLACK
    private var radius = 0f
    private var leftRadius = 0f
    private var rightRadius = 0f
    private var topRadius = 0f
    private var bottomRadius = 0f
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f
    private var rtvBgColor = Color.WHITE
    private var startBgColor = 0
    private var endBgColor = 0

    constructor(context: Context): super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundStyleTextView)

        rtvBorderWidth = attributes.getDimensionPixelSize(R.styleable.RoundStyleTextView_strokeWidth, 0)
        rtvUseEnableAlpha = attributes.getBoolean(R.styleable.RoundStyleTextView_useEnableAlpha, true)
        rtvBorderColor = attributes.getColor(R.styleable.RoundStyleTextView_strokeColor, Color.BLACK)
        radius = attributes.getDimension(R.styleable.RoundStyleTextView_radius, 0f)
        leftRadius = attributes.getDimension(R.styleable.RoundStyleTextView_leftRadius, 0f)
        rightRadius = attributes.getDimension(R.styleable.RoundStyleTextView_rightRadius, 0f)
        topRadius = attributes.getDimension(R.styleable.RoundStyleTextView_topRadius, 0f)
        bottomRadius = attributes.getDimension(R.styleable.RoundStyleTextView_bottomRadius, 0f)
        topLeftRadius = attributes.getDimension(
            R.styleable.RoundStyleTextView_topLeftRadius,
            if (topRadius == 0f) leftRadius else topRadius
        )
        topRightRadius = attributes.getDimension(
            R.styleable.RoundStyleTextView_topRightRadius,
            if (topRadius == 0f) rightRadius else topRadius
        )
        bottomLeftRadius = attributes.getDimension(
            R.styleable.RoundStyleTextView_bottomLeftRadius,
            if (bottomRadius == 0f) leftRadius else bottomRadius
        )
        bottomRightRadius = attributes.getDimension(
            R.styleable.RoundStyleTextView_bottomRightRadius,
            if (bottomRadius == 0f) rightRadius else bottomRadius
        )
        rtvBgColor = attributes.getColor(R.styleable.RoundStyleTextView_bgColor, Color.WHITE)
        startBgColor = attributes.getColor(R.styleable.RoundStyleTextView_startBgColor, 0)
        endBgColor = attributes.getColor(R.styleable.RoundStyleTextView_endBgColor, 0)
        attributes.recycle()

        initView(context)
    }

    private fun initView(context: Context){
        val gd: GradientDrawable
        if (startBgColor != 0 && endBgColor != 0) {
            gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startBgColor, endBgColor)
            )
            gd.gradientType = GradientDrawable.LINEAR_GRADIENT
        } else {
            gd = GradientDrawable() //创建drawable
            gd.setColor(rtvBgColor)
        }
        if (radius > 0) {
            gd.cornerRadius = radius
        } else {
            val radiusx = floatArrayOf(
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
            )
            gd.cornerRadii = radiusx
        }

        if (rtvBorderWidth > 0) {
            gd.setStroke(rtvBorderWidth, rtvBorderColor)
        }

        this.background = gd

        setEnableState(isEnabled)
    }

    fun setBackgroungColor(@ColorInt color: Int) {
        val myGrad = background as GradientDrawable
        myGrad.setColor(color)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setEnableState(enabled)
    }

    fun setEnableState(enabled: Boolean){
        if (rtvUseEnableAlpha) {
            alpha = if (enabled) 1f else 0.6f
        }
    }

    fun setBorder(borderWidth: Int, borderColor: Int){
        val myGrad = background as GradientDrawable
        myGrad.setStroke(borderWidth, borderColor)
    }

    fun bindEnableByEditor(func: (() -> Boolean), vararg edits: EditText){
        if(edits.isEmpty()) return
        CustomTextWatcher.createImpl(object : CustomTextWatcher() {
            override fun onTextChanged(editText: EditText?, context: String?) {
                isEnabled = func()
            }
        }, *edits)
    }
}