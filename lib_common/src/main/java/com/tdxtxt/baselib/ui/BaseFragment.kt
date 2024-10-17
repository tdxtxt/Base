package com.tdxtxt.baselib.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.dialog.impl.ProgressDialog
import com.tdxtxt.baselib.rx.transformer.ProgressTransformer
import com.tdxtxt.baselib.rx.transformer.UIThreadTransformer
import com.tdxtxt.baselib.tools.FragmentTrackHelper
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.baselib.ui.viewbinding.ViewBindingWrapper
import com.tdxtxt.baselib.view.titlebar.OnTitleBarListener
import com.tdxtxt.baselib.view.titlebar.TitleBar
import com.tdxtxt.baselib.view.viewstate.StateLayout
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.android.FragmentEvent
import com.trello.rxlifecycle3.components.support.RxFragment

abstract class BaseFragment : RxFragment(), IView {
    open var fragmentActivity: FragmentActivity? = null
    protected lateinit var mRootView: View
    private var mContainer: ViewGroup? = null
    private var stateLayouts = SparseArray<StateLayout>()
    private var mProgressDialog: ProgressDialog? = null
    private var mTitleBar: TitleBar? = null
    private var realMenuCallBack: MenuCallBack? = null
    protected var interceptBackEvent = false
    protected var interceptCallBack: (() -> Unit)? = null
    internal val viewbindingWrapper by lazy { ViewBindingWrapper<ViewBinding>() }

    abstract fun getLayoutId(): Int
    /**
     * 布局中TitleBar控件id默认R.id.titlebar，若自定义id，需要重写此方法
     */
    open fun getToolBarResId() = R.id.titlebar

    open fun initUi(){}

    open fun getParams(bundle: Bundle?){}

    open fun reload(){}

    open fun customConfigSateView(view: View, stateLayout: StateLayout){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getParams(arguments)
        FragmentTrackHelper.trackFragmentCreated(this, arguments)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivity = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContainer = container
        mRootView = inflater.inflate(getLayoutId(), container, false)
        if(this is IViewBinding<*>){
            setViewBindingRoot(mRootView)
        }
        mRootView.isClickable = true //截断点击时间段扩散，防止多Fragment出现重叠以及点击穿透
        FragmentTrackHelper.trackFragmentViewCreated(this, mRootView, savedInstanceState)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
    }
    override fun onResume() {
        super.onResume()
        FragmentTrackHelper.trackFragmentResume(this)
    }

    override fun onPause() {
        super.onPause()
        FragmentTrackHelper.trackFragmentPause(this)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        FragmentTrackHelper.trackFragmentSetUserVisibleHint(this, isVisibleToUser)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        FragmentTrackHelper.trackFragmentOnHiddenChanged(this, hidden)
    }

    fun <T : View> findViewOrNull(resId: Int): T? {
        return mRootView.findViewById<View>(resId) as T?
    }

    fun <T : View> findView(resId: Int): T {
        return mRootView.findViewById<View>(resId) as T
    }

    fun getStateView(resId: Int) : StateLayout{
        var stateLayout: StateLayout? = stateLayouts.get(resId)

        if(stateLayout != null) return stateLayout

        if(resId == mRootView.id){
            throw IllegalArgumentException("作用控件不能为根View，可根据情况在包一层(原因会影响部分功能)")
        }

        val view: View = mRootView.findViewById(resId)?: throw IllegalArgumentException("作用控件不存在，请检查resId是否正确")

        if(fragmentActivity != null){
            stateLayout = StateLayout(fragmentActivity!!)

            initConfigStateView(view, stateLayout)
            stateLayout.showContent()
            stateLayouts.put(resId, stateLayout)
            return stateLayout
        }
        throw IllegalArgumentException("activity is null")
    }

    private fun initConfigStateView(view: View, stateLayout: StateLayout){
        stateLayout.configAll(
            retryAction = {
                //点击errorView的回调
                reload()
            })

        customConfigSateView(view, stateLayout)
        stateLayout.wrap(view)
    }

    override fun getProgressBar(): ProgressDialog? {
        if (mProgressDialog == null){
            when(fragmentActivity){
                is BaseActivity -> {
                    val activity: BaseActivity = fragmentActivity as BaseActivity
                    mProgressDialog = activity.getProgressBar()
                }
                else -> {
                    if(fragmentActivity == null || fragmentActivity?.isFinishing() != false || fragmentActivity?.isDestroyed != false){
                        return null
                    }
                    mProgressDialog = ProgressDialog.createProgressDialog(fragmentActivity!!)
                }
            }
        }
        mProgressDialog?.setDesc(getString(R.string.加载中))?.apply { setCancelable(ProgressDialog.mGlobalCancelable).setCancelableOnTouchOutside(ProgressDialog.mGlobalOutsideCancelable) }
        return mProgressDialog
    }

    override fun showProgressBar() {
        getProgressBar()?.show()
    }

    override fun showProgressBar(desc: String, isCancel: Boolean) {
        getProgressBar()?.setDesc(desc)?.setCancelable(isCancel)?.show();
    }

    override fun <T> bindLifecycle(): LifecycleTransformer<T> {
        return this.bindUntilEvent(FragmentEvent.DESTROY);
    }

    override fun <T> bindUIThread(): UIThreadTransformer<T> {
        return UIThreadTransformer()
    }

    override fun <T> bindProgress(): ProgressTransformer<T> {
        return ProgressTransformer(getProgressBar());
    }

    override fun <T> bindProgress(bindDialog: Boolean): ProgressTransformer<T> {
        return ProgressTransformer(getProgressBar(), bindDialog);
    }

    override fun hideProgressBar() {
        getProgressBar()?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideProgressBar()
        stateLayouts.clear()
        if(this is IViewBinding<*>){
            viewbindingDestory()
        }
        FragmentTrackHelper.trackFragmentDestroyView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentTrackHelper.trackFragmentDestroy(this)
        interceptCallBack = null
        realMenuCallBack = null
    }

    inline fun <reified T : Activity> getParentActivity() : T?{
        if(fragmentActivity == null) return null
        if(fragmentActivity is T) return fragmentActivity as T
        return null
    }

    open fun setTitleBar(title: String?) {
        getTitleBar()?.apply {
            this.title = title
        }
    }

    /**
     * 初始化TitleBar
     */
    fun initTitleBar(){
        getTitleBar()?.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View) {
                interceptCallBack?.invoke()
                if (!interceptBackEvent) fragmentActivity?.onBackPressed()
            }
            override fun onTitleClick(v: View) {
            }
            override fun onRightClick(v: View) {
                realMenuCallBack?.click?.invoke()
            }
        })
    }

    /**
     * 使用前须调用初始化方法{initTitleBar}
     */
    open fun setTitleBar(title: String?, rightMenu: (MenuCallBack.() -> Unit)) {
        setTitleBar(title)
        setTitleBarRight(rightMenu)
    }

    /**
     * 使用前须调用初始化方法{initTitleBar}
     */
    open fun setTitleBarRight(rightMenu: (MenuCallBack.() -> Unit)? = null){
        getTitleBar()?.apply {
            if(rightMenu == null){
                rightView.visibility = View.GONE
            }else{
                realMenuCallBack = object : MenuCallBack(){ }
                realMenuCallBack?.apply {
                    rightMenu()
                    if(isTextMenu()){
                        rightView.visibility = View.VISIBLE
                        rightTitle = menuText
                    }else if(isIconMenu()){
                        rightView.visibility = View.VISIBLE
                        setRightIcon(icon)
                    }
                }
            }
        }
    }

    /**
     * 拦截返回事件
     */
    fun setInterceptBackEvent(interceptBackEvent: Boolean, interceptCallBack: (() -> Unit)? = null) {
        this.interceptBackEvent = interceptBackEvent
        this.interceptCallBack = interceptCallBack
    }

    /**
     * 获取TitleBar控件
     */
    fun getTitleBar(): TitleBar? {
        if(mTitleBar == null) mTitleBar = findView(getToolBarResId())
        return mTitleBar
    }

    /**
     * 添加到fragment层：仅支持父布局为 FrameLayout ConstraintLayout RelativeLayout的视图
     */
    fun addContentViewToFragment(view: View){
        if(view.parent != null) return
        if(mRootView is FrameLayout){
            (mRootView as FrameLayout).addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }else if(mRootView is ConstraintLayout){
            val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            (mRootView as ConstraintLayout).addView(view, lp)
        }else if(mRootView is RelativeLayout){
            (mRootView as RelativeLayout).addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    fun removeContentViewFromFragment(view: View){
        if(mRootView is FrameLayout){
            (mRootView as FrameLayout).removeView(view)
        }else if(mRootView is ConstraintLayout){
            (mRootView as ConstraintLayout).removeView(view)
        }else if(mRootView is RelativeLayout){
            (mRootView as RelativeLayout).removeView(view)
        }
    }

    /**
     * 添加到activity层：仅支持父布局为 FrameLayout ConstraintLayout RelativeLayout的视图
     */
    fun addContentViewToParent(view: View){
        if(view.parent != null) return
        if(mContainer is FrameLayout){
            (mContainer as FrameLayout).addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }else if(mContainer is ConstraintLayout){
            val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            (mContainer as ConstraintLayout).addView(view, lp)
        }else if(mContainer is RelativeLayout){
            (mContainer as RelativeLayout).addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    fun removeContentViewFromParent(view: View){
        if(mContainer is FrameLayout){
            (mContainer as FrameLayout).removeView(view)
        }else if(mContainer is ConstraintLayout){
            (mContainer as ConstraintLayout).removeView(view)
        }else if(mContainer is RelativeLayout){
            (mContainer as RelativeLayout).removeView(view)
        }
    }

    fun getRootView() = mRootView

    fun getContainer() = mContainer

}