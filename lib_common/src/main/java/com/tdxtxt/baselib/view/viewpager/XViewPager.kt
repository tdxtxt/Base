package com.tdxtxt.baselib.view.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ReflectUtils
import com.tdxtxt.baselib.R

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/18
 *     desc   :
 * </pre>
 */
class XViewPager : ViewPager {
    private var supportHeightWrap = false
    private var supportScroll = true

    constructor(context: Context): super(context){
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.XViewPager)
        supportHeightWrap = attributes.getBoolean(R.styleable.XViewPager_xvp_height_wrap, false)
        supportScroll = attributes.getBoolean(R.styleable.XViewPager_xvp_horizontal_scroll, true)
        attributes.recycle()
        initView(context)
    }

    private fun initView(context: Context){}

    fun setHorizontalScroll(supportScroll: Boolean){
        this.supportScroll = supportScroll
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(supportHeightWrap){
            var height = 0
            (0 until childCount).forEach {
                val childView = getChildAt(it)
                childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                val childHeight = childView.measuredHeight
                if(childHeight > height){
                    height = childHeight
                }
            }
            val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return supportScroll && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return supportScroll && super.onInterceptTouchEvent(ev)
    }

    inline fun <reified T : Fragment> getCurrentFragment() = getChildFragment<T>(currentItem)

    inline fun <reified T : Fragment> getChildFragment(position: Int): T?{
        val adapter = adapter
        val fm: FragmentManager? = if(adapter is FragmentPagerAdapter){
            ReflectUtils.reflect(adapter).field("mFragmentManager").get<FragmentManager>()
        }else if(adapter is FragmentStatePagerAdapter){
            ReflectUtils.reflect(adapter).field("mFragmentManager").get<FragmentManager>()
        }else null
        return fm?.findFragmentByTag("android:switcher:$id:$position") as T?
    }
}