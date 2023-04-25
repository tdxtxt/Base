package com.tdxtxt.tablayout.horizontal

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tdxtxt.tablayout.R
import com.tdxtxt.tablayout.tools.TabUtils
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/24
 *     desc   :
 * </pre>
 */
class TabView: ConstraintLayout, IMeasurablePagerTitleView {
    private var mSelectedColor: Int = Color.BLUE
    private var mNormalColor: Int = Color.GRAY
    private var selectedBold = false
    private var normalBold = false
    private var mTextSelectSize = 0f
    private var mTextUnselectSize = 0f

    private var mTextSizeScale = true
    private var mSelectedScale = 1f
    private var mNormalScale = 1f
    private var mDiffScale = 0f

    private lateinit var textView: TextView
    private lateinit var tvBadge: TextView
    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        initView(context)
    }

    private fun initView(context: Context){
        LayoutInflater.from(context).inflate(R.layout.tablib_item_xsliding_tab, this, true)
        textView = findViewById(R.id.tabview)
        tvBadge = findViewById(R.id.badgeview)
    }


    fun setTabTextView(tab: CharSequence?){
        textView.text = tab?: ""
    }

    fun setBradge(bradge: CharSequence?){
        tvBadge.text = bradge?: ""
    }

    fun getTabTextView() = textView

    fun getBradgeView() = tvBadge

    fun setTextSize(normalSize: Float, selectedSize: Float, textSizeScale: Boolean){
        mTextUnselectSize = if(normalSize == 0f) TabUtils.dp2px(14f) else normalSize
        mTextSelectSize = if(selectedSize == 0f) mTextUnselectSize else selectedSize
        mTextSizeScale = textSizeScale
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSelectSize)
        if(textSizeScale){
            mDiffScale = (mTextSelectSize - mTextUnselectSize) / mTextSelectSize
            mNormalScale = mTextUnselectSize / mTextSelectSize
            mSelectedScale = 1f
        }
    }

    fun setTextBold(normalBold: Boolean, selectedBold: Boolean){
        this.normalBold = normalBold
        this.selectedBold = selectedBold
    }

    fun setTextColor(normalColor: Int, selectedColor: Int){
        this.mSelectedColor = selectedColor
        this.mNormalColor = normalColor
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if(selectedBold){
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        }else{
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        }

        if(!mTextSizeScale){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSelectSize)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if(normalBold){
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        }else{
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        }

        if(!mTextSizeScale){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextUnselectSize)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor)
        textView.setTextColor(color)
        if(!mTextSizeScale) return
        if(mDiffScale == 0f) return
        textView.setScaleX(mSelectedScale - mDiffScale * leavePercent)
        textView.setScaleY(mSelectedScale - mDiffScale * leavePercent)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor)
        textView.setTextColor(color)
        if(!mTextSizeScale) return
        if(mDiffScale == 0f) return
        textView.setScaleX(mNormalScale + mDiffScale * enterPercent)
        textView.setScaleY(mNormalScale + mDiffScale * enterPercent)
    }

    override fun getContentLeft(): Int {
        val bound = Rect()
        var longestString = ""
        if (textView.getText().toString().contains("\n")) {
            val brokenStrings: Array<String> =
                textView.getText().toString().split("\\n".toRegex()).toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = textView.getText().toString()
        }
        textView.getPaint().getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width()

//        val badgeBound = Rect()
//        tvBadge.paint.getTextBounds(tvBadge.text.toString(), 0, tvBadge.length(), badgeBound)
//        val badgeWidth = badgeBound.width()

        return left + width / 2 - contentWidth / 2
    }

    override fun getContentTop(): Int {
        val metrics: Paint.FontMetrics = textView.getPaint().getFontMetrics()
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val bound = Rect()
        var longestString = ""
        if (textView.getText().toString().contains("\n")) {
            val brokenStrings: Array<String> =
                textView.getText().toString().split("\\n".toRegex()).toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = textView.getText().toString()
        }
        textView.getPaint().getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width()

//        val badgeBound = Rect()
//        tvBadge.paint.getTextBounds(tvBadge.text.toString(), 0, tvBadge.length(), badgeBound)
//        val badgeWidth = badgeBound.width()

        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics: Paint.FontMetrics = textView.getPaint().getFontMetrics()
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 + contentHeight / 2).toInt()
    }
}