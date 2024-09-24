package com.tdxtxt.baselib.view.recycler.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextUtils
import android.util.ArrayMap
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlin.math.abs
import kotlin.math.max


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-09-26
 *     desc   : 列表悬浮，可以参考微信账单日期悬浮效果。缺点：支持点击事件有限，悬浮布局如果是ViewGroup及其子类的容器，仅支持子控件的点击
 * </pre>
 */
abstract class FloatItemDecoration(val recyclerView: RecyclerView?, private val mHeight: Int) : ItemDecoration() {
    private val mDecorationTouchListener by lazy { DecorationTouchListener(recyclerView, this) }
    private val mFloatItemViews by lazy { ArrayMap<String, View>() }
    private val mFloatItemRects by lazy { SparseArray<Rect>() }
    abstract fun getGroupId(position: Int): String?
    abstract fun getLayoutResId(): Int
    open fun convert(floatView: View, position: Int){ }
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        var groupId = safeGetGroupId(position)
        if (TextUtils.isEmpty(groupId)) return
        //只有是同一组的第一个才显示悬浮栏
        if (firstInGroup(position)) {
            outRect.top = mHeight
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount = state.itemCount
        val childCount = parent.childCount
        val left = parent.left + parent.paddingLeft
        val right = parent.right - parent.paddingRight
        var preGroupId: String? //标记上一个item对应的Group
        var currentGroupId: String? = null //当前item对应的Group
        mFloatItemRects.clear()

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            if(position == RecyclerView.NO_POSITION) continue
            preGroupId = currentGroupId
            currentGroupId = safeGetGroupId(position)
            if (currentGroupId == null || TextUtils.equals(currentGroupId, preGroupId)) continue

            val viewBottom = view.bottom
            var bottom = max(mHeight, view.top) //top 决定当前顶部第一个悬浮Group的位置
            if (position + 1 < itemCount) {
                //获取下个GroupName
                val nextGroupId: String? = safeGetGroupId(position + 1)
                //下一组的第一个View接近头部
                if (currentGroupId != nextGroupId && viewBottom < bottom) {
                    bottom = viewBottom
                }
            }
            //根据top绘制group
            val top = bottom - mHeight
            mFloatItemRects.put(position, Rect(left, top, right, bottom))
            val floatItemView = mFloatItemViews[currentGroupId]?: createFloatItemView(position, parent).apply {
                mFloatItemViews.put(currentGroupId, this)
            }
            convert(floatItemView, position)
            fixLayoutSize(floatItemView, left, top, right, bottom)
            drawFloatItemView(c, floatItemView, left, top)
        }
    }

    internal fun findFloatPositionUnder(x: Int, y: Int): Int{
        mFloatItemRects.forEach { position, rect ->
            if(rect.contains(x, y)){
                return position
            }
        }
        return -1
    }

    internal fun isClickChildView(view: View?, x: Int, y: Int): Boolean {
        if(view == null) return false
        if(!view.hasOnClickListeners()) return false
        mFloatItemRects.forEach { position, parentRect ->
            if(parentRect.contains(x, y)){
                val childRect = Rect(parentRect.left + view.left, parentRect.top + view.top, parentRect.left + view.left + view.width, parentRect.top + view.top + view.height)
                return childRect.contains(x, y)
            }
        }
        return false
    }

    internal fun findFloatViewUnder(position: Int?): View? {
        if(position == null) return null
        if(position == -1) return null
        return mFloatItemViews.get(safeGetGroupId(position))
    }

    private fun firstInGroup(position: Int): Boolean {
        if (position == 0) {
            return true
        }
        val prevGroupId: String? = safeGetGroupId(position - 1)
        val groupId: String? = safeGetGroupId(position)
        return !TextUtils.equals(prevGroupId, groupId)
    }

    private fun drawFloatItemView(c: Canvas, view: View, left: Int, top: Int){
        c.save()
        c.translate(left.toFloat(),  top.toFloat())
        view.draw(c)
        c.restore()
    }

    open fun fixLayoutSize(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(abs(right - left), View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(abs(bottom - top), View.MeasureSpec.EXACTLY)

        view.measure(widthSpec, heightSpec)
        view.layout(left, top, right, bottom)
    }

    private fun safeCheckPosition(position: Int): Boolean {
        val adapter = recyclerView?.adapter?: return false
        if(adapter is BaseQuickAdapter<*, *>){
            if(position < 0 || position >= adapter.data.size) return false
        }
        return true
    }
    private fun safeGetGroupId(position: Int): String? {
        if(safeCheckPosition(position)) return getGroupId(position)
        return null
    }
    open fun createFloatItemView(position: Int, recyclerView: RecyclerView): View{
        return LayoutInflater.from(recyclerView.context).inflate(getLayoutResId(), recyclerView, false)
    }
    fun setOnClickListener(view: View?, listener: View.OnClickListener){
        if(view == null) return
        recyclerView?.removeOnItemTouchListener(mDecorationTouchListener)
        recyclerView?.addOnItemTouchListener(mDecorationTouchListener)
        view.setOnClickListener(listener)
    }

}