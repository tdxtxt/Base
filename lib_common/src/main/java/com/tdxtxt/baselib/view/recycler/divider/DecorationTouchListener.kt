package com.tdxtxt.baselib.view.recycler.divider

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-09-27
 *     desc   :
 * </pre>
 */
internal class DecorationTouchListener(val recyclerView: RecyclerView?, val decoration: FloatItemDecoration?) : RecyclerView.OnItemTouchListener, GestureDetector.SimpleOnGestureListener() {
    private val mGestureDetector by lazy { GestureDetector(recyclerView?.context, this) }
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        if(e == null) return false
        val position = decoration?.findFloatPositionUnder(e.x.toInt(), e.y.toInt())
        if(position != null && position != -1){
            val floatView = decoration?.findFloatViewUnder(position)
            if(floatView is ViewGroup){
                val childViewCount = floatView.childCount
                if(childViewCount == 0) return true
                for(index in 0 until childViewCount){
                    val childView = floatView.getChildAt(index)
                    if(decoration?.isClickChildView(childView, e.x.toInt(), e.y.toInt()) == true){
                        childView?.callOnClick()
                        return true
                    }
                }
            }else{
                floatView?.callOnClick()
            }
            return true
        }
        return false
    }

}