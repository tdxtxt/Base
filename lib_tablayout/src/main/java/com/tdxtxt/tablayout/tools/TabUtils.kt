package com.tdxtxt.tablayout.tools

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tdxtxt.tablayout.R
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

    fun bindRecyclerView(magicIndicator: MagicIndicator?, recyclerView: RecyclerView?){
        recyclerView?.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //当由recyclerView触发时，isScroll 置true
                if (event?.getActionMasked() == MotionEvent.ACTION_DOWN || event?.getActionMasked() == MotionEvent.ACTION_MOVE ) {
                    recyclerView.setTag(R.id.tablayout_mark, true) //recyclerView主动引起的滑动，这里表示手势操作引起的滑动
                }
                return false
            }
        })
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.getTag(R.id.tablayout_mark) == false) { //recyclerView被动引起的滑动，这里表示点击tablayout引起的滑动
                    return@onScrolled
                }

                val layoutManager = recyclerView.layoutManager
                if(layoutManager is LinearLayoutManager){
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    magicIndicator?.onPageSelected(firstVisibleItemPosition)
                }
            }
        })
    }

    fun smoothScrollPosition2Top(recyclerView: RecyclerView?, position: Int){
        if(recyclerView == null) return
        val manager = recyclerView.layoutManager
        if(manager is LinearLayoutManager){
//            manager.scrollToPositionWithOffset(position, 0)
            val smoothScroller = object : LinearSmoothScroller(recyclerView.context){
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            //如果选中的条目不在显示范围内，要滑动条目让该条目显示出来
            smoothScroller.targetPosition = position
            if(smoothScroller.targetPosition >= 0) manager.startSmoothScroll(smoothScroller)
        }
    }
}