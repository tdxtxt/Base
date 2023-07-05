package com.tdxtxt.baselib.view.viewstate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.SizeUtils
import com.tdxtxt.baselib.R

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/29
 *     desc   : 状态布局
 * </pre>
 */
class StateLayout : FrameLayout {
    private var state = State.None
    private var loadingView: View? = null
    private var emptyView: View? = null
    private var errorView: View? = null
    private var contentView: View? = null

    private var loadingLayoutId = StateLayout.loadingLayoutId
    private var emptyLayoutId = StateLayout.emptyLayoutId
    private var errorLayoutId = StateLayout.errorLayoutId

    private var animDuration = 250L
    private var isEmptyViewClick = false
    private var mRetryAction: (() -> Unit)? = null
    private var switchTask: SwitchTask? = null
    private var lastSwitchStateTime = 0L
    private var topPadding: Int? = null

    constructor(context: Context): super(context)

    fun configAll(loadingLayoutId : Int? = null, emptyLayoutId : Int? = null, errorLayoutId: Int? = null, retryAction: (() -> Unit)? = null){
        if(loadingLayoutId != null ) this.loadingLayoutId = loadingLayoutId
        if(emptyLayoutId != null ) this.emptyLayoutId = emptyLayoutId
        if(errorLayoutId != null ) this.errorLayoutId = errorLayoutId
        mRetryAction = retryAction
    }

    fun configTopPadding(top: Float?){
        if(top == null) topPadding = null
        else topPadding = SizeUtils.dp2px(top)
    }

    fun wrap(view: View?): StateLayout {
        if(view == null) return this

        setLoadingLayout()
        setEmptyLayout()
        setErrorLayout()

        view.visibility = View.INVISIBLE
        view.alpha = 0f
        if(view.parent == null) {
            //no attach parent.
            addView(view, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            contentView = view
        }else {
            // 1.remove self from parent
            val parent = view.parent as ViewGroup
            val lp = view.layoutParams
            val index = parent.indexOfChild(view)
            parent.removeView(view)
            // 2.wrap view as a parent
            addView(view, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

            // 3.add this to original parent，暂不支持parent为ConstraintLayout
            parent.addView(this, index, lp)
            contentView = view
        }
        switchLayout(State.Content)
        return this
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            if(contentView == null) contentView = getChildAt(0)
//            setLoadingLayout()
//            setEmptyLayout()
//            setErrorLayout()
        }
    }

    private fun setLoadingLayout(): StateLayout{
        if (loadingView?.parent != null) removeView(loadingView)
        loadingView = LayoutInflater.from(context).inflate(loadingLayoutId, this, false)
        loadingView?.apply {
            (layoutParams as LayoutParams).apply {
                gravity = Gravity.CENTER
                if(topPadding != null){
                    setPadding(0, top, 0, 0)
                }
            }
            visibility = View.GONE
            alpha = 0f
            addView(this)
        }
        return this
    }
    private fun setEmptyLayout(){
        if (emptyView?.parent != null) removeView(emptyView)
        emptyView = LayoutInflater.from(context).inflate(emptyLayoutId, this, false)
        emptyView?.apply {
            (layoutParams as LayoutParams).apply {
                gravity = Gravity.CENTER
                if(topPadding != null){
                    setPadding(0, top, 0, 0)
                }
            }
            visibility = View.GONE
            alpha = 0f
            addView(this)
        }

        (emptyView?.findViewById<View?>(R.id.btn_retry)?: this).setOnClickListener {
            if(isEmptyViewClick) retry()
        }
    }
    private fun setErrorLayout(){
        if (errorView?.parent != null) removeView(errorView)
        errorView = LayoutInflater.from(context).inflate(errorLayoutId, this, false)

        errorView?.apply {
            (layoutParams as LayoutParams).apply {
                gravity = Gravity.CENTER
                if(topPadding != null){
                    setPadding(0, top, 0, 0)
                }
            }
            visibility = View.GONE
            alpha = 0f
            addView(this)
        }

        (errorView?.findViewById<View?>(R.id.btn_retry)?: this).setOnClickListener {
            retry()
        }
    }

    private fun retry(){
        showLoading()
        postDelayed({
            mRetryAction?.invoke()
        }, animDuration)
    }

    private fun switchLayout(s: State, useContentBg: Boolean? = null){
        if (state == s) return
        state = s
        when (state) {
            State.Loading -> {
                switch(loadingView)
                loadingView?.setBackgroundColor(Color.TRANSPARENT)
            }
            State.Empty -> {
                switch(emptyView)
                if (useContentBg == true) emptyView?.setBackgroundColor(Color.TRANSPARENT)
            }
            State.Error -> {
                switch(errorView)
                if (useContentBg == true) errorView?.setBackgroundColor(Color.TRANSPARENT)
            }
            State.Content -> {
                if (contentView?.visibility == VISIBLE && loadingView?.visibility != VISIBLE
                    && emptyView?.visibility != VISIBLE && errorView?.visibility != VISIBLE
                ) return
                switch(contentView)
            }
            else -> {

            }
        }
    }

    private fun switch(v: View?){
        if (switchTask != null) {
            removeCallbacks(switchTask)
        }
        switchTask = SwitchTask(v)
        post(switchTask)
    }

    private inner class SwitchTask(private var target: View?) : Runnable {
        override fun run() {
            for (i in 0..childCount) {
                if (state == State.Loading && getChildAt(i) == contentView) continue
                hideAnim(getChildAt(i))
            }
            showAnim(target)
        }
    }

    private fun showAnim(v: View?) {
        if (v == null) return
        v.animate().cancel()
        v.animate().alpha(1f).setDuration(animDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    v.visibility = View.VISIBLE
                }
            })
            .start()
    }

    private fun hideAnim(v: View?) {
        if (v == null) return
        v.animate().cancel()
        v.animate().alpha(0f).setDuration(animDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    v.visibility = if(v==contentView) View.INVISIBLE else View.GONE
                }
            })
            .start()
    }

    /**
     * 保证两次切换状态时间差至少300毫秒，否则切换无效
     */
    private fun createSwitchTimeDiff(): Long{
        if(Math.abs(System.currentTimeMillis() - lastSwitchStateTime) > 300){
            lastSwitchStateTime = System.currentTimeMillis()
            return 0
        }else{
            lastSwitchStateTime = System.currentTimeMillis()
            return 300
        }
    }

    fun showLoading(){
        post {
            switchLayout(State.Loading)
        }
    }

    fun showContent(){
        if(state == State.None) return
        postDelayed({ switchLayout(State.Content) }, createSwitchTimeDiff())
    }

    fun showEmpty(text: CharSequence? = null, @DrawableRes icon: Int? = null, isEmptyViewClick: Boolean = false, useContentBg: Boolean = true){
        this.isEmptyViewClick = isEmptyViewClick
        postDelayed( {
            if(icon != null){
                emptyView?.findViewById<ImageView>(R.id.ivNoDataIcon)?.setImageResource(icon)
            }
            if(text != null){
                emptyView?.findViewById<TextView>(R.id.tvNoDataText)?.text = text
            }
            switchLayout(State.Empty, useContentBg)
        }, createSwitchTimeDiff())
    }

    fun showError(text: CharSequence? = null, @DrawableRes icon: Int? = null, useContentBg: Boolean = true){
        postDelayed( {
            if(icon != null){
                errorView?.findViewById<ImageView>(R.id.ivErrorIcon)?.setImageResource(icon)
            }
            if(text != null){
                errorView?.findViewById<TextView>(R.id.tvErrorText)?.text = text
            }
            switchLayout(State.Error, useContentBg)
        }, createSwitchTimeDiff())
    }

    companion object{
        private var loadingLayoutId = R.layout.baselib_statelayout_loading
        private var emptyLayoutId = R.layout.baselib_statelayout_empty
        private var errorLayoutId = R.layout.baselib_statelayout_error

        fun configLayout(loadingLayoutId : Int? = null, emptyLayoutId : Int? = null, errorLayoutId: Int? = null){
            if(loadingLayoutId != null) StateLayout.loadingLayoutId = loadingLayoutId
            if(emptyLayoutId != null) StateLayout.emptyLayoutId = emptyLayoutId
            if(errorLayoutId != null) StateLayout.errorLayoutId = errorLayoutId
        }
    }

}

