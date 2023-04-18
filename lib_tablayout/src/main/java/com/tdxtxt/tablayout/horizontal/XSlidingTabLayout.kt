package com.tdxtxt.tablayout.horizontal

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.tdxtxt.tablayout.R
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import com.tdxtxt.tablayout.tools.TabUtils


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/13
 *     desc   :
 * </pre>
 */
class XSlidingTabLayout : MagicIndicator {
    private var mTitles: List<String>? = null
    private var mViewPager: ViewPager? = null

    private var mIndicatorColor: Int = ContextCompat.getColor(context, R.color.tl_indicator_color)
    private var mIndicatorHeight: Float = TabUtils.dp2px(3f)
    private var mIndicatorWidth: Float = 0f
    private var mIndicatorRadius: Float = TabUtils.dp2px(4f)
    private var mIndicatorOffsetY: Float = 0f

    private var mTextSelectSize: Float = TabUtils.dp2px(14f)
    private var mTextUnselectSize: Float = TabUtils.dp2px(14f)
    private var mTextSelectColor: Int = ContextCompat.getColor(context, R.color.tl_select_color)
    private var mTextUnselectColor: Int = ContextCompat.getColor(context, R.color.tl_unselect_color)
    private var mTextSelectBold: Boolean = false
    private var mTextUnselectBold: Boolean = false
    private var mTextSizeScale: Boolean = true

    private var mTabEqual: Boolean = false
    private var mTabHorizontalMargin: Float = 0f
    private var mTabHorizontalPadding: Float = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.XSlidingTabLayout)
        mIndicatorColor = attributes.getColor(
            R.styleable.XSlidingTabLayout_tl_indicator_color,
            ContextCompat.getColor(context, R.color.tl_indicator_color)
        )
        mIndicatorHeight = attributes.getDimension(
            R.styleable.XSlidingTabLayout_tl_indicator_height,
            TabUtils.dp2px(3f)
        )
        mIndicatorOffsetY = attributes.getDimension(R.styleable.XSlidingTabLayout_tl_indicator_offset_bottom, 0f)

        mIndicatorWidth =
            attributes.getDimension(R.styleable.XSlidingTabLayout_tl_indicator_width, 0f)
        mIndicatorRadius =
            attributes.getDimension(R.styleable.XSlidingTabLayout_tl_indicator_corner_radius,
            TabUtils.dp2px(4f))

        mTextSelectSize = attributes.getDimension(
            R.styleable.XSlidingTabLayout_tl_textSelectSize,
            TabUtils.sp2px(14f)
        )
        mTextUnselectSize = attributes.getDimension(
            R.styleable.XSlidingTabLayout_tl_textUnselectSize,
            TabUtils.sp2px(14f)
        )
        mTextSelectColor = attributes.getColor(
            R.styleable.XSlidingTabLayout_tl_textSelectColor,
            ContextCompat.getColor(context, R.color.tl_select_color)
        )
        mTextUnselectColor = attributes.getColor(
            R.styleable.XSlidingTabLayout_tl_textUnselectColor,
            ContextCompat.getColor(context, R.color.tl_unselect_color)
        )
        mTextSelectBold = attributes.getBoolean(R.styleable.XSlidingTabLayout_tl_textSelectBold, false)
        mTextUnselectBold = attributes.getBoolean(R.styleable.XSlidingTabLayout_tl_textUnselectBold, false)
        mTextSizeScale = attributes.getBoolean(R.styleable.XSlidingTabLayout_tl_textSizeScale, true)

        mTabEqual = attributes.getBoolean(R.styleable.XSlidingTabLayout_tl_tab_space_equal, false)
        mTabHorizontalMargin = attributes.getDimension(R.styleable.XSlidingTabLayout_tl_tab_horizontal_margin, 0f)
        mTabHorizontalPadding = attributes.getDimension(R.styleable.XSlidingTabLayout_tl_tab_horizontal_padding,
            TabUtils.dp2px(12f)
        )

        attributes.recycle()

        if(isInEditMode){
            setViewPager(ViewPager(context), listOf("Tab一", "Tab二", "Tab三", "Tab四"))
        }
    }


    fun setViewPager(viewPager: ViewPager) {
        val adapter = viewPager.adapter ?: return
        val count = adapter.count
        if (count == 0) return
        setViewPager(viewPager, (0 until count).map { adapter.getPageTitle(it)?.toString() ?: "" })
    }

    fun setViewPager(viewPager: ViewPager, titles: List<String>?) {
        mTitles = titles
        mViewPager = viewPager
        val navigator = createNavigator()
        setNavigator(navigator)

        // must after setNavigator
        val titleContainer = navigator.titleContainer
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerDrawable = object : ColorDrawable(){
            override fun getIntrinsicWidth(): Int {
                return mTabHorizontalPadding.toInt()
            }
        }
        titleContainer.setPadding(mTabHorizontalMargin.toInt(), 0, mTabHorizontalMargin.toInt(), 0)

        ViewPagerHelper.bind(this, viewPager)
    }

    private fun createNavigator(): CommonNavigator {
        val navigator = CommonNavigator(context)
        navigator.setAdjustMode(mTabEqual)

        navigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount() = mTitles?.size ?: 0

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val titleView = ScaleTransitionPagerTitleView(context)
                titleView.setTextColor(mTextUnselectColor, mTextSelectColor)
                titleView.setTextBold(mTextUnselectBold, mTextSelectBold)
                titleView.setTextSize(mTextUnselectSize, mTextSelectSize, mTextSizeScale)

                titleView.text = mTitles?.get(index)

                titleView.setOnClickListener {
                    mViewPager?.currentItem = index
                }
                return titleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                if (mIndicatorWidth > 0) {
                    indicator.mode = LinePagerIndicator.MODE_EXACTLY
                    indicator.lineWidth = mIndicatorWidth
                } else {
                    indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                }

                indicator.setColors(mIndicatorColor)
                indicator.lineHeight = mIndicatorHeight
                indicator.roundRadius = mIndicatorRadius
                indicator.yOffset = mIndicatorOffsetY
                return indicator
            }
        }
        return navigator
    }




}