package com.tdxtxt.baselib.view.viewstate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SizeUtils
import com.tdxtxt.baselib.R

class StateLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attributeSet, defStyleAttr) {
    private var state = State.None // default state
    private var loadingView: View? = null
    private var emptyView: View? = null
    private var errorView: View? = null
    private var contentView: View? = null
    private var animDuration = 250L
    private var emptyText: String = ""
    private var emptyIcon: Int = 0
    private var errorText: String = ""
    private var errorIcon: Int = 0
    private var enableTouchWhenLoading = false
    private var defaultShowLoading = false
    private var noEmptyAndError = false //是否去除empty和error状态，有时候只需要一个loading状态，这样减少内存
    private var showLoadingOnce = false //是否只显示一次Loading
    private var retryAutoLoading: Boolean = true // 错误重试是否自动显示loadding
    private var loadingLayoutId = 0
    private var emptyLayoutId = 0
    private var errorLayoutId = 0
    private var paddingTopDp: Int? = null
    private var paddingBottomDp: Int? = null
    private var hasShowLoading = false
    private var lastSwitchStateTime = 0L;
    private var isEmptyViewRetryEnable = true//空试图需要点击重试功能吗

    init {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.StateLayout)
        loadingLayoutId = ta.getResourceId(R.styleable.StateLayout_sl_loadingLayoutId,
            StateLayoutConfig.loadingLayoutId
        )
        emptyLayoutId = ta.getResourceId(R.styleable.StateLayout_sl_emptyLayoutId,
            StateLayoutConfig.emptyLayoutId
        )
        errorLayoutId = ta.getResourceId(R.styleable.StateLayout_sl_errorLayoutId,
            StateLayoutConfig.errorLayoutId
        )
        animDuration = ta.getInt(R.styleable.StateLayout_sl_animDuration, StateLayoutConfig.animDuration.toInt()).toLong()
        enableTouchWhenLoading = ta.getBoolean(R.styleable.StateLayout_sl_enableTouchWhenLoading,
            StateLayoutConfig.enableTouchWhenLoading
        )
        defaultShowLoading = ta.getBoolean(R.styleable.StateLayout_sl_defaultShowLoading,
            StateLayoutConfig.defaultShowLoading
        )
        noEmptyAndError = ta.getBoolean(R.styleable.StateLayout_sl_noEmptyAndError,
            StateLayoutConfig.noEmptyAndError
        )
        showLoadingOnce = ta.getBoolean(R.styleable.StateLayout_sl_showLoadingOnce,
            StateLayoutConfig.showLoadingOnce
        )
        emptyText = ta.getString(R.styleable.StateLayout_sl_emptyText) ?: StateLayoutConfig.emptyText
        emptyIcon = ta.getResourceId(R.styleable.StateLayout_sl_emptyIcon,
            StateLayoutConfig.emptyIcon
        )

        ta.recycle()
    }

    fun wrap(view: View?): StateLayout {
        if (view == null) {
            throw IllegalArgumentException("view can not be null")
        }

        setLoadingLayout()
        setEmptyLayout()
        setErrorLayout()

        view.visibility = View.INVISIBLE
        view.alpha = 0f
        if (view.parent == null) {
            //no attach parent.
            addView(view, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            contentView = view
        } else {
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
        switchLayout(if (defaultShowLoading) State.Loading else State.Content)
        return this
    }

    fun wrap(activity: Activity): StateLayout = wrap((activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0))

    fun wrap(fragment: Fragment): StateLayout = wrap(fragment.view)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            contentView = getChildAt(0)
            setLoadingLayout()
            setEmptyLayout()
            setErrorLayout()
            switchLayout(if (defaultShowLoading) State.Loading else State.Content)
        }
    }

    private fun switchLayout(s: State, useContentBg: Boolean = true) {
        if(state==s)return
        state = s
        when (state) {
            State.Loading -> {
                switch(loadingView)
                loadingView?.setBackgroundColor(Color.TRANSPARENT)
            }
            State.Empty -> {
                switch(emptyView)
                if (useContentBg) {
                    emptyView?.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            State.Error -> {
                switch(errorView)
                if (useContentBg) {
                    errorView?.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            State.Content -> {
                if(contentView?.visibility==VISIBLE && loadingView?.visibility!=VISIBLE
                        && emptyView?.visibility!=VISIBLE && errorView?.visibility!=VISIBLE)return
                switch(contentView)
            }
        }
    }

    fun showLoading(): StateLayout {
        if(showLoadingOnce && hasShowLoading) return this

        post {
            switchLayout(State.Loading)
            if(showLoadingOnce) hasShowLoading = true
        }
        return this
    }

    fun showContent(): StateLayout {
        if(state == State.None) return this
        postDelayed({ switchLayout(State.Content) }, createSwitchTimeDiff())
        return this
    }

    fun showEmpty(useContentBg: Boolean = true): StateLayout {
        postDelayed( {
            if(noEmptyAndError) {
                switchLayout(State.Content)
            }else{
                switchLayout(State.Empty, useContentBg)
            }
        }, createSwitchTimeDiff())
        return this
    }

    fun showError(useContentBg: Boolean = true): StateLayout {
        postDelayed( {
            if(noEmptyAndError) {
                switchLayout(State.Content)
            }else{
                switchLayout(State.Error, useContentBg)
            }
        }, createSwitchTimeDiff())
        return this
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

    private fun switch(v: View?) {
        if (switchTask != null) {
            removeCallbacks(switchTask)
        }
        switchTask = SwitchTask(v)
        post(switchTask)
    }

    private fun retry(retryView: View?) {
        if (retryView == null) return
        if(retryAutoLoading){
            hasShowLoading = false
            showLoading()
            postDelayed({
                mRetryAction?.invoke(retryView)
            }, animDuration)
        }else{
            mRetryAction?.invoke(retryView)
        }
    }

    var switchTask: SwitchTask? = null

    inner class SwitchTask(private var target: View?) : Runnable {
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (state == State.Loading && loadingView?.visibility == View.VISIBLE && !enableTouchWhenLoading) return true
        return super.dispatchTouchEvent(ev)
    }

    private var mRetryAction: ((errView: View) -> Unit)? = null

    /**
     * 设置加载中的布局
     */
    private fun setLoadingLayout(): StateLayout {
        if (loadingView?.parent != null) removeView(loadingView)
        loadingView = LayoutInflater.from(context).inflate(loadingLayoutId, this, false)
        loadingView?.apply {
            (layoutParams as LayoutParams).gravity = Gravity.CENTER
            visibility = View.GONE
            alpha = 0f
            addView(loadingView)
        }
        return this
    }

    /**
     * 设置数据为空的布局
     */
    private fun setEmptyLayout(): StateLayout {
        if(noEmptyAndError)return this
        if (emptyView?.parent != null) removeView(emptyView)
        emptyView = LayoutInflater.from(context).inflate(emptyLayoutId, this, false)
        emptyView?.apply {

            (layoutParams as LayoutParams).apply {
                gravity = Gravity.CENTER
                setPadding(paddingLeft, paddingTopDp?: paddingTop, paddingRight, paddingBottomDp?: paddingBottom)
            }
            visibility = View.GONE
            alpha = 0f
            addView(emptyView)

            //智能设置文字和图标
            if(emptyView!=null && emptyView is ViewGroup){
                val group = emptyView as ViewGroup
                (0 until group.childCount).forEach {
                    val child = group.getChildAt(it)
                    if(child is TextView && !emptyText.isEmpty()) {
                        child.text = emptyText
                    }else if(child is ImageView && emptyIcon != 0){
                        child.setImageResource(emptyIcon)
                    }
                }
            }

            if(isEmptyViewRetryEnable) (findViewById<View?>(R.id.btn_retry)?: this).setOnClickListener { retry(emptyView) }
        }
        return this
    }

    /**
     * 设置加载失败的布局
     */
    private fun setErrorLayout(): StateLayout {
        if(noEmptyAndError)return this
        if (errorView?.parent != null) removeView(errorView)
        errorView = LayoutInflater.from(context).inflate(errorLayoutId, this, false)
        errorView?.apply {
            (layoutParams as LayoutParams).apply {
                gravity = Gravity.CENTER
                setPadding(paddingLeft, paddingTopDp?: paddingTop, paddingRight, paddingBottomDp?: paddingBottom)
            }
            visibility = View.GONE
            alpha = 0f
            addView(errorView)

            //智能设置文字和图标
            if(errorView!=null && errorView is ViewGroup){
                val group = errorView as ViewGroup
                (0 until group.childCount).forEach {
                    val child = group.getChildAt(it)
                    if(child is TextView && !errorText.isEmpty()) {
                        child.text = errorText
                    }else if(child is ImageView && errorIcon != 0){
                        child.setImageResource(errorIcon)
                    }
                }
            }

            (findViewById<View?>(R.id.btn_retry)?: this).setOnClickListener { retry(errorView) }
        }
        return this
    }

    /**
     * 自定义一些配置
     * @param loadingLayoutId 加载时的布局
     * @param emptyLayoutId 数据为空时的布局
     * @param errorLayoutId 加载失败的布局
     * @param useContentBgWhenLoading 是否在加载状态下使用contentView的背景
     * @param animDuration 遮照显示和隐藏的动画时长
     * @param enableLoadingShadow 是否启用加载时的半透明阴影
     * @param enableTouchWhenLoading 是否在加载时允许触摸下层View
     * @param retryAction 加载失败状态下点击重试的行为
     */
    fun configAll(loadingLayoutId: Int? = null,
                  emptyLayoutId: Int? = null,
                  errorLayoutId: Int? = null,
                  emptyText: String? = null,
                  emptyIcon: Int? = null,
                  errorText: String? = null,
                  errorIcon: Int? = null,
                  animDuration: Long? = null,
                  noEmptyAndError: Boolean? = null,
                  defaultShowLoading: Boolean? = null,
                  enableTouchWhenLoading: Boolean? = null,
                  showLoadingOnce: Boolean? = null,
                  paddingTop: Int? = null,
                  paddingBottom: Int? = null,
                  retryAutoLoading: Boolean = true,
                  isEmptyViewRetryEnable: Boolean = false,
                  retryAction: ((errView: View) -> Unit)? = null): StateLayout {
        if(emptyText!=null) this.emptyText = emptyText
        if(emptyIcon!=null) this.emptyIcon = emptyIcon
        if(errorText!=null) this.errorText = errorText
        if(errorIcon!=null) this.errorIcon = errorIcon
        if(noEmptyAndError!=null) this.noEmptyAndError = noEmptyAndError
        if (loadingLayoutId != null) {
            this.loadingLayoutId = loadingLayoutId
            setLoadingLayout()
        }
        if (emptyLayoutId != null) this.emptyLayoutId  = emptyLayoutId
        if(emptyLayoutId!=null || emptyText!=null || emptyIcon!=null){
            setEmptyLayout()
        }
        if (errorLayoutId != null){
            this.errorLayoutId = errorLayoutId
            setErrorLayout()
        }

        if (animDuration != null) {
            this.animDuration = animDuration
        }
        if (paddingBottom != null) paddingBottomDp = SizeUtils.dp2px(paddingBottom.toFloat())
        if (paddingTop != null) paddingTopDp = SizeUtils.dp2px(paddingTop.toFloat())
        if (defaultShowLoading != null) this.defaultShowLoading = defaultShowLoading
        if (enableTouchWhenLoading != null) this.enableTouchWhenLoading = enableTouchWhenLoading
        if (showLoadingOnce != null) this.showLoadingOnce = showLoadingOnce
        this.retryAutoLoading = retryAutoLoading
        this.isEmptyViewRetryEnable = isEmptyViewRetryEnable
        if(retryAction!=null) mRetryAction = retryAction
        return this
    }

    /**
     * 配置空视图
     */
    fun configEmptyLayoutId(emptyLayoutId: Int? = null, emptyText: String? = null, emptyIcon: Int? = null, isEmptyViewRetryEable: Boolean = false){
        this.isEmptyViewRetryEnable = isEmptyViewRetryEable
        if (emptyLayoutId != null) this.emptyLayoutId  = emptyLayoutId
        if(emptyLayoutId!=null || emptyText!=null || emptyIcon!=null){
            if(emptyText!=null) this.emptyText = emptyText
            if(emptyIcon!=null) this.emptyIcon = emptyIcon
            setEmptyLayout()
        }
    }

    fun configEmpty(emptyText: String? = null, emptyIcon: Int? = null, isEmptyViewRetryEable: Boolean = false){
        this.isEmptyViewRetryEnable = isEmptyViewRetryEable
        if (emptyText != null || emptyIcon != null){
            if (emptyText != null) this.emptyText = emptyText
            if (emptyIcon != null) this.emptyIcon = emptyIcon
            setEmptyLayout()
        }
    }

    /**
     * 配置错误视图
     */
    fun configErrorLayoutId(errorLayoutId: Int? = null, errorText: String? = null, errorIcon: Int? = null, retryAutoLoading:Boolean = true){
        if (errorLayoutId != null) this.errorLayoutId = errorLayoutId
        if(errorLayoutId!=null || errorText!=null || errorIcon!=null){
            if(errorText!=null) this.errorText = errorText
            if(errorIcon!=null) this.errorIcon = errorIcon
            setErrorLayout()
        }

        this.retryAutoLoading = retryAutoLoading
    }

    fun configError(errorText: String? = null, errorIcon: Int? = null){
        if (errorText != null || errorIcon != null){
            if (errorText != null) this.errorText = errorText
            if (errorIcon != null) this.errorIcon = errorIcon
            setEmptyLayout()
        }
    }

    /**
     * 配置间距，可用于微调, 单位pd，仅适用于空视图和错误视图的间距调整
     */
    fun configPadding(paddingTop: Int?, paddingBottom: Int?){
        if(paddingBottom != null) paddingBottomDp = SizeUtils.dp2px(paddingBottom.toFloat())
        if(paddingTop != null) paddingTopDp = SizeUtils.dp2px(paddingTop.toFloat())
    }

    fun getEmptyView() = emptyView
    fun getErrorView() = errorView
}