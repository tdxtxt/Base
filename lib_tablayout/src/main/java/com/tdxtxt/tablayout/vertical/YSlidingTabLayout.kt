package com.tdxtxt.tablayout.vertical

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.tdxtxt.tablayout.R
import com.tdxtxt.tablayout.tools.TabUtils

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/17
 *     desc   :
 * </pre>
 */
class YSlidingTabLayout : FrameLayout {
    private lateinit var mRecyclerView: RecyclerView

    private var mIndicatorColor: Int = ContextCompat.getColor(context, R.color.tl_indicator_color)
    private var mIndicatorHeight: Float = TabUtils.dp2px(20f)
    private var mIndicatorWidth: Float = 0f
    private var mIndicatorRadius: Float = TabUtils.dp2px(4f)

    private var mTextSelectSize: Float = TabUtils.dp2px(14f)
    private var mTextUnselectSize: Float = TabUtils.dp2px(14f)
    private var mTextSelectColor: Int = ContextCompat.getColor(context, R.color.tl_select_color)
    private var mTextUnselectColor: Int = ContextCompat.getColor(context, R.color.tl_unselect_color)
    private var mTextSelectBold: Boolean = false
    private var mTextUnselectBold: Boolean = false
    private var mTextMaxLine = -1 //总共显示多少行-1表示自适应
    private var mTextLineLength = -1 //每一行最多显示字数-1表示自适应

    private var mBackgroundSelectColor: Int = ContextCompat.getColor(context, android.R.color.white)
    private var mBackgroundUnselectColor: Int = ContextCompat.getColor(context, android.R.color.white)

    private var mTabHeight: Float = 0f
    private var mTabVerticalPadding: Float = 0f
    private var mTabHorizontalPadding: Float = 0f

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.YSlidingTabLayout)
        mIndicatorColor = attributes.getColor(
            R.styleable.YSlidingTabLayout_tl_indicator_color,
            ContextCompat.getColor(context, R.color.tl_indicator_color)
        )
        mIndicatorHeight = attributes.getDimension(
            R.styleable.YSlidingTabLayout_tl_indicator_height,
            TabUtils.dp2px(20f)
        )

        mIndicatorWidth =
            attributes.getDimension(R.styleable.YSlidingTabLayout_tl_indicator_width, 0f)
        mIndicatorRadius =
            attributes.getDimension(R.styleable.YSlidingTabLayout_tl_indicator_corner_radius,
                TabUtils.dp2px(4f))


        mTextMaxLine = attributes.getInteger(R.styleable.YSlidingTabLayout_tl_textMaxLine, -1)
        mTextLineLength = attributes.getInteger(R.styleable.YSlidingTabLayout_tl_textLineLength, -1)
        mTextSelectSize = attributes.getDimension(
            R.styleable.YSlidingTabLayout_tl_textSelectSize,
            TabUtils.sp2px(14f)
        )
        mTextUnselectSize = attributes.getDimension(
            R.styleable.YSlidingTabLayout_tl_textUnselectSize,
            TabUtils.sp2px(14f)
        )
        mTextSelectColor = attributes.getColor(
            R.styleable.YSlidingTabLayout_tl_textSelectColor,
            ContextCompat.getColor(context, R.color.tl_select_color)
        )
        mTextUnselectColor = attributes.getColor(
            R.styleable.YSlidingTabLayout_tl_textUnselectColor,
            ContextCompat.getColor(context, R.color.tl_unselect_color)
        )
        mTextSelectBold = attributes.getBoolean(R.styleable.YSlidingTabLayout_tl_textSelectBold, false)
        mTextUnselectBold = attributes.getBoolean(R.styleable.YSlidingTabLayout_tl_textUnselectBold, false)

        mTabHeight = attributes.getDimension(R.styleable.YSlidingTabLayout_tl_tab_height, 0f)
        mTabVerticalPadding = attributes.getDimension(R.styleable.YSlidingTabLayout_tl_tab_vertical_padding,
            TabUtils.dp2px(12f)
        )
        mTabHorizontalPadding = attributes.getDimension(R.styleable.YSlidingTabLayout_tl_tab_horizontal_padding, 0f)

        mBackgroundSelectColor = attributes.getColor(R.styleable.YSlidingTabLayout_tl_backgroundSelectColor, ContextCompat.getColor(context, android.R.color.white))
        mBackgroundUnselectColor = attributes.getColor(R.styleable.YSlidingTabLayout_tl_backgroundUnselectColor, ContextCompat.getColor(context, android.R.color.white))

        attributes.recycle()

        initView(context)
    }

    private fun initView(context: Context){
        mRecyclerView = RecyclerView(context)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.itemAnimator?.apply {
            addDuration = 0
            changeDuration = 0
            removeDuration = 0
        }
        mRecyclerView.adapter = SimpleTabAdapter(this)

        addView(mRecyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        if(isInEditMode){
            setViewPager(ViewPager(context), listOf("Tab一", "Tab二", "Tab三", "Tab四", "Tab五", "Tab六", "Tab七", "Tab八", "Tab九", "Tab十", "Tab十一"))
        }
    }

    /**
     * 参考 https://mp.weixin.qq.com/s/UK7Is0DDcUmzC7CTB8gyvQ
     */
    private fun scrollToPosition(position: Int){
        val manager = mRecyclerView.layoutManager
        if(manager is LinearLayoutManager){
            //如果选中的条目不在显示范围内，要滑动条目让该条目显示出来
            val startVisible = manager.findFirstVisibleItemPosition()
            val endVisible = manager.findLastVisibleItemPosition()
            if(position <= startVisible){
                TabUtils.rvScrollToPosition(mRecyclerView, startVisible - 1)
                //这个方法的作用是定位到指定项，就是把你想显示的项显示出来，但是在屏幕的什么位置是不管的，只要那一项现在看得到了，那它就罢工了！
//                manager.scrollToPosition(startVisible - 1)
            }else if(position >= endVisible){
                TabUtils.rvSmoothScrollToPosition(mRecyclerView, startVisible + 1)
                //这种方式是定位到指定项如果该项可以置顶就将其置顶显示
//                manager.scrollToPositionWithOffset(startVisible + 1, 0)
            }
        }

    }

    fun setViewPager(viewPager: ViewPager) {
        val adapter = viewPager.adapter ?: return
        val count = adapter.count
        if (count == 0) return
        setViewPager(viewPager, (0 until count).map { adapter.getPageTitle(it)?.toString() ?: "" })
    }

    fun getSimpleTabAdapter(): SimpleTabAdapter? {
        val adapter = mRecyclerView.adapter
        if(adapter is SimpleTabAdapter){
           return adapter
        }
        return null
    }

    fun setViewPager(viewPager: ViewPager, titles: List<String>?) {
        getSimpleTabAdapter()?.apply {
            setData(titles)
            setItemClickListener(object : SimpleTabAdapter.OnItemClickListener {
                override fun itemClick(view: View, position: Int) {
                    viewPager.currentItem = position
                }
            })
        }

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
            override fun onPageSelected(position: Int) {
                getSimpleTabAdapter()?.checkIndex(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    class SimpleTabAdapter(val tabLayout: YSlidingTabLayout) : RecyclerView.Adapter<SimpleTabAdapter.TabViewHolder>(){
        private var titles: List<String>? = null
        private var clickListener: OnItemClickListener? = null
        private var checkIndex = 0

        fun checkIndex(position: Int){
            tabLayout.scrollToPosition(position)
            val lastCheckIndex = checkIndex
            checkIndex = position
            notifyItemChanged(lastCheckIndex)
            notifyItemChanged(checkIndex)
        }

        fun setData(titles: List<String>?){
            this.titles = titles
            notifyDataSetChanged()
        }

        fun setItemClickListener(listener: OnItemClickListener){
            clickListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
            val holder = TabViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.baselib_item_ysliding_tab, parent, false))
            holder.mTab?.setOnClickListener { clickListener?.itemClick(holder.itemView, holder.adapterPosition) }
            if (tabLayout.mTabHeight > 0)
                holder.mTab?.apply {
                    layoutParams = layoutParams.apply {
                        height = tabLayout.mTabHeight.toInt()
                    }
                }
            if(tabLayout.mTextMaxLine > 0){
                holder.mTab?.maxLines = tabLayout.mTextMaxLine
                holder.mTab?.ellipsize = TextUtils.TruncateAt.END
            }
            holder.mTab?.setPadding(tabLayout.mTabHorizontalPadding.toInt(), tabLayout.mTabVerticalPadding.toInt(), tabLayout.mTabHorizontalPadding.toInt(), tabLayout.mTabVerticalPadding.toInt())
            holder.mIndicator?.setParmas(tabLayout.mIndicatorWidth, tabLayout.mIndicatorHeight, tabLayout.mIndicatorColor, tabLayout.mIndicatorRadius)
            return holder
        }

        override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
            if(tabLayout.mTextLineLength > 0){
                holder.mTab?.setText((titles?.get(position) as java.lang.String?)?.replaceAll("(.{${tabLayout.mTextLineLength}})", "$1\n")?.trim())
            }else{
                holder.mTab?.setText(titles?.get(position))
            }
            holder.itemView.setBackgroundColor(if(isCheck(position)) tabLayout.mBackgroundSelectColor else tabLayout.mBackgroundUnselectColor)
            holder.mTab?.setTextSize(TypedValue.COMPLEX_UNIT_PX, if(isCheck(position)) tabLayout.mTextSelectSize else tabLayout.mTextUnselectSize)
            holder.mTab?.setTextColor(if(isCheck(position)) tabLayout.mTextSelectColor else tabLayout.mTextUnselectColor)

            if(isCheck(position)){
                holder.mTab?.setTypeface(if(tabLayout.mTextSelectBold) Typeface.defaultFromStyle(Typeface.BOLD) else Typeface.defaultFromStyle(Typeface.NORMAL))
            }else{
                holder.mTab?.setTypeface(if(tabLayout.mTextUnselectBold) Typeface.defaultFromStyle(Typeface.BOLD) else Typeface.defaultFromStyle(Typeface.NORMAL))
            }

            holder.mIndicator?.visibility = if(isCheck(position)) View.VISIBLE else View.INVISIBLE
        }

        fun isCheck(position: Int) = position == checkIndex

        override fun getItemCount() = titles?.size?: 0

        interface OnItemClickListener{
            fun itemClick(view: View, position: Int)
        }

        class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mTab: TextView? = null
            var mIndicator: TabIndicator? = null
            init {
                mTab = itemView.findViewById(R.id.tv_tab)
                mIndicator = itemView.findViewById(R.id.indicator)
            }

        }
    }
}