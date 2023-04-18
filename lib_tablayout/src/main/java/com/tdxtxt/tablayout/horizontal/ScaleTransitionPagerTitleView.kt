package com.tdxtxt.tablayout.horizontal

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import com.tdxtxt.tablayout.tools.TabUtils

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/13
 *     desc   : 带颜色渐变和缩放的指示器标题
 * </pre>
 */
class ScaleTransitionPagerTitleView(context: Context) : ColorTransitionPagerTitleView(context) {
    private var selectedBold = false
    private var normalBold = false
    private var mTextSelectSize = 0f
    private var mTextUnselectSize = 0f

    private var mTextSizeScale = true
    private var mSelectedScale = 1f
    private var mNormalScale = 1f
    private var mDiffScale = 0f

    init {
        setPadding(0, 0, 0, 0)
    }

    fun setTextSize(normalSize: Float, selectedSize: Float, textSizeScale: Boolean){
        mTextUnselectSize = if(normalSize == 0f) TabUtils.dp2px(14f) else normalSize
        mTextSelectSize = if(selectedSize == 0f) mTextUnselectSize else selectedSize
        mTextSizeScale = textSizeScale
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSelectSize)
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
        setNormalColor(normalColor)
        setSelectedColor(selectedColor)
    }

    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)
        if(selectedBold){
            setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        }else{
            setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        }

        if(!mTextSizeScale){
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSelectSize)
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        super.onDeselected(index, totalCount)
        if(normalBold){
            setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        }else{
            setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        }

        if(!mTextSizeScale){
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextUnselectSize)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        super.onLeave(index, totalCount, leavePercent, leftToRight)
        if(!mTextSizeScale) return
        if(mDiffScale == 0f) return
        setScaleX(mSelectedScale - mDiffScale * leavePercent)
        setScaleY(mSelectedScale - mDiffScale * leavePercent)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        super.onEnter(index, totalCount, enterPercent, leftToRight)
        if(!mTextSizeScale) return
        if(mDiffScale == 0f) return
        setScaleX(mNormalScale + mDiffScale * enterPercent)
        setScaleY(mNormalScale + mDiffScale * enterPercent)
    }

}