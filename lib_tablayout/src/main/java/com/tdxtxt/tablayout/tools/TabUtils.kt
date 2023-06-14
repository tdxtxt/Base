package com.tdxtxt.tablayout.tools

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import net.lucode.hackware.magicindicator.MagicIndicator


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/17
 *     desc   :
 * </pre>
 */
object TabUtils {
    @JvmStatic
    fun dp2px(dpValue: Int) = dp2px(dpValue.toFloat())
    @JvmStatic
    fun dp2px(dpValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f)
    }
    @JvmStatic
    fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }
    @JvmStatic
    fun px2dp(pxValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f)
    }

    fun bindViewPage2(magicIndicator: MagicIndicator?, viewPager: ViewPager2?){
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                magicIndicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magicIndicator?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                magicIndicator?.onPageScrollStateChanged(state)
            }
        })
    }

    //https://mp.weixin.qq.com/s?__biz=MzAxNjg3MTIyMw==&mid=2650482117&idx=1&sn=4c4a16020d5db83f2e24c1654a44fe82&scene=21#wechat_redirect
    @SuppressLint("ClickableViewAccessibility")
    fun bindScrollView(magicIndicator: MagicIndicator?, scollview: NestedScrollView?, anchorViews: List<View?>?){
        val tops = anchorViews?.map { it?.top?: -1 } ?: return
        scollview?.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //当由scrollview触发时，isScroll 置true
                if (event?.getAction() == MotionEvent.ACTION_DOWN) {
                    scollview.tag = true //scrollview主动引起的滑动，这里表示手势操作引起的滑动
                }
                return false
            }
        })
        scollview?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scollview.tag == false) { //scrollview被动引起的滑动，这里表示点击tablayout引起的滑动
                return@OnScrollChangeListener
            }

            for (index in tops.size - 1 downTo 0) {
                //根据滑动距离，对比各模块距离父布局顶部的高度判断
                if (scrollY > tops[index] - 10) {
                    magicIndicator?.onPageSelected(index)
                    break
                }
            }
        })
    }

    /**
     * 缓慢滚动
     */
    fun rvSmoothScrollToPosition(recyclerView: RecyclerView?, position: Int) {
        if(recyclerView == null) return
        val tempLayoutManager = recyclerView.layoutManager
        val layoutManager: LinearLayoutManager? = if(tempLayoutManager is LinearLayoutManager) tempLayoutManager else null
        if(layoutManager == null) return

        var smoothScrolling = true

        val firstPos: Int = layoutManager.findFirstVisibleItemPosition()
        val lastPos: Int = layoutManager.findLastVisibleItemPosition()

        if (position in (firstPos + 1) until lastPos) {
            val childAt: View? = layoutManager.findViewByPosition(position)
            val top = childAt?.top ?: 0
            recyclerView.smoothScrollBy(0, top)
        } else {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (smoothScrolling || newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (position in layoutManager.findFirstVisibleItemPosition() + 1..layoutManager.findLastVisibleItemPosition()) {
                            val childAt: View? = layoutManager.findViewByPosition(position)
                            val top = childAt?.top ?: 0
                            recyclerView.scrollBy(0, top)
                            recyclerView.removeOnScrollListener(this)
                        }
                        smoothScrolling = false
                    }
                }
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
            })
            recyclerView.smoothScrollToPosition(position)
        }

    }


    /**
     * 直接跳转刷新Layout
     */
    fun rvScrollToPosition(recyclerView: RecyclerView?, position: Int) {
        if(recyclerView == null) return
        val tempLayoutManager = recyclerView.layoutManager
        val layoutManager: LinearLayoutManager? = if(tempLayoutManager is LinearLayoutManager) tempLayoutManager else null
        if(layoutManager == null) return

        val firstPos = layoutManager.findFirstVisibleItemPosition()
        val lastPos: Int = layoutManager.findLastVisibleItemPosition()

        if (position <= firstPos) {
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(position)
        } else if (position <= lastPos) {
            //当要置顶的项已经在屏幕上显示时,通过LayoutManager
            val childAt: View? = layoutManager.findViewByPosition(position)
            val top = childAt?.top ?: 0
            recyclerView.scrollBy(0, top)
        } else {
            //当要置顶的项在当前显示的最后一项之后
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }
}