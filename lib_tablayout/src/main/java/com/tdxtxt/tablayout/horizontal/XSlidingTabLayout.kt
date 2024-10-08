package com.tdxtxt.tablayout.horizontal

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.tdxtxt.tablayout.R
import com.tdxtxt.tablayout.listener.OnTabClickListener
import com.tdxtxt.tablayout.tools.TabUtils
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/13
 *     desc   :
 * </pre>
 */
class XSlidingTabLayout : MagicIndicator {
    private var mTitles: List<String>? = null
    private var mInsideTabClickListener: OnTabClickListener? = null
    private var mOutsideTabClickListener: OnTabClickListener? = null

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
    private var mTabLabelsId = 0
    private var mTabPosition = 0

    constructor(context: Context) : super(context) {
        initView(context)
    }
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
        if(attributes.hasValue(R.styleable.XSlidingTabLayout_tl_tab_labels)){
            mTabLabelsId = attributes.getResourceId(R.styleable.XSlidingTabLayout_tl_tab_labels, 0)
        }
        mTabPosition = attributes.getInt(R.styleable.XSlidingTabLayout_tl_tab_position, 0)
        attributes.recycle()

        initView(context)
    }

    private fun initView(context: Context){
        if(mTabLabelsId != 0){
            mTitles = listOf(*resources.getStringArray(mTabLabelsId))
        }

        if(isInEditMode){
            if(mTitles?.isNotEmpty() != true){
                mTitles = listOf("Tab1", "Tab2", "Tab3", "Tab4")
            }
//            setRecyclerView(RecyclerView(context), mTitles)
//            setViewPager(ViewPager(context), mTitles)
        }

        if(mTitles?.isNotEmpty() == true){
            setTabText(mTitles, mTabPosition, null)
        }
    }

    fun setTabText(titles: List<String>?, defaultSelectPosition: Int? = null, tabClickLisenter: OnTabClickListener? = null){
        mTitles = titles
        mInsideTabClickListener = tabClickLisenter
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

        if(defaultSelectPosition != null){
            onPageSelected(defaultSelectPosition)
            mInsideTabClickListener?.onTabClick(defaultSelectPosition)
        }
    }

    fun setRecyclerView(recyclerView: RecyclerView, titles: List<String>?, smoothScroll: Boolean = false){
        mTitles = titles
        mInsideTabClickListener = object : OnTabClickListener{
            override fun onTabClick(position: Int) {
                recyclerView.apply {
                    setTag(R.id.tablayout_mark, false) //recyclerView被动引起的滑动，这里表示点击tablayout引起的滑动
                    TabUtils.smoothScrollPosition2Top(this, position)
                }
            }
        }

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
        TabUtils.bindRecyclerView(this, recyclerView)
    }

    fun setViewPager2(viewPager: ViewPager2, titles: List<String>?, smoothScroll: Boolean = false){
        mTitles = titles
        mInsideTabClickListener = object : OnTabClickListener{
            override fun onTabClick(position: Int) {
                viewPager.setCurrentItem(position, smoothScroll)
            }
        }
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
        TabUtils.bindViewPage2(this, viewPager)
    }

    fun setViewPager(viewPager: ViewPager, smoothScroll: Boolean = false) {
        val adapter = viewPager.adapter ?: return
        val count = adapter.count
        if (count == 0) return
        setViewPager(viewPager, (0 until count).map { adapter.getPageTitle(it)?.toString() ?: "" }, smoothScroll)
    }

    fun setViewPager(viewPager: ViewPager, titles: List<String>?, smoothScroll: Boolean = false) {
        mTitles = titles
        mInsideTabClickListener = object : OnTabClickListener{
            override fun onTabClick(position: Int) {
                viewPager.setCurrentItem(position, smoothScroll)
            }
        }
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

    fun setTabClickListener(listener: OnTabClickListener?){
        mOutsideTabClickListener = listener
    }

    fun getTabView(position: Int): TabView?{
        val nav = navigator
        if(nav is CommonNavigator){
            val tabView = nav.getPagerTitleView(position)
            if(tabView is TabView)
                return tabView
        }
        return null
    }

    private fun createNavigator(): CommonNavigator {
        val navigator = CommonNavigator(context)
        navigator.setAdjustMode(mTabEqual)

        navigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount() = mTitles?.size ?: 0

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val tabView = TabView(context)
                tabView.setTextColor(mTextUnselectColor, mTextSelectColor)
                tabView.setTextBold(mTextUnselectBold, mTextSelectBold)
                tabView.setTextSize(mTextUnselectSize, mTextSelectSize, mTextSizeScale)
                tabView.setTabTextView(mTitles?.getOrNull(index))
                tabView.setEqual(mTabEqual)
                tabView.setOnClickListener {
                    navigator.onPageSelected(index)
                    mInsideTabClickListener?.onTabClick(index)
                    mOutsideTabClickListener?.onTabClick(index)
                }
                return tabView
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