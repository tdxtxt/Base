package com.tdxtxt.baselib.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.KeyboardUtils
import com.tdxtxt.baselib.dialog.impl.ProgressDialog
import com.tdxtxt.baselib.rx.transformer.ProgressTransformer
import com.tdxtxt.baselib.rx.transformer.UIThreadTransformer
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding
import com.tdxtxt.baselib.ui.viewbinding.ViewBindingWrapper
import com.tdxtxt.baselib.view.viewstate.StateLayout
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.android.ActivityEvent
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity


/**
 * @作者： ton
 * @创建时间： 2018\11\30
 * @功能描述： 所有activity的基类，必须继承它(强制),封装类容:调整方法
 * @传入参数说明： 无
 * @返回参数说明： 无
 */
abstract class BaseActivity : RxAppCompatActivity(), IView {
    protected lateinit var fragmentActivity: FragmentActivity
    private var mProgressDialog: ProgressDialog? = null
    protected var autoHideSoftInput = false
    protected var dispatchTouchEventCallBack: ((ev: MotionEvent?) -> Unit)? = null
    protected var interceptBackEvent = false
    protected var interceptCallBack: (() -> Unit)? = null
    private val stateLayouts = SparseArray<StateLayout>()
    internal val viewbindingWrapper by lazy { ViewBindingWrapper<ViewBinding>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = this
        parseParams(intent) //解析参数
        if (getLayoutResId() != 0) {
            if (this is IViewBinding<*>) { //当前继承了viewBinding代理类
                val rootView = layoutInflater.inflate(getLayoutResId(), null, false)
                setViewBindingRoot(rootView)
                setContentView(rootView)
            } else {
                setContentView(getLayoutResId())
            }
        }
        initStatusBar()
        initUi()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseParams(intent) //解析参数
    }

    abstract fun getLayoutResId(): Int
    open fun initUi(){}
    open fun reload(){}
    open fun customConfigSateView(view: View, stateLayout: StateLayout){}

    private fun parseParams(intent: Intent?) {
        if (intent == null) return
        var extraBundle: Bundle? = intent.getBundleExtra("Bundle")
        if (extraBundle == null) extraBundle = intent.extras
        getParams(extraBundle)
    }
    open fun getParams(bundle: Bundle?){}

    /**
     * 多状态通用页面
     */
    fun getStateView(resId: Int) : StateLayout{
        var stateLayout: StateLayout? = stateLayouts.get(resId)

        if(stateLayout != null) return stateLayout

        val view: View = findViewById(resId)?: (findViewById<View>(android.R.id.content))

        stateLayout = StateLayout(fragmentActivity)

        initConfigStateView(view, stateLayout)
        stateLayout.showContent()
        stateLayouts.put(resId, stateLayout)
        return stateLayout
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

    /**
     * 状态栏
     */
    open fun initStatusBar(){
        StatusBarHelper.setStatusBarFullTransparent(this, false)
    }

    override fun getProgressBar(): ProgressDialog? {
        if (mProgressDialog == null) mProgressDialog = ProgressDialog.createProgressDialog(this, "正在加载...")
        return mProgressDialog?.setDesc("正在加载...")?.apply { setCancelable(ProgressDialog.mGlobalCancelable).setCancelableOnTouchOutside(ProgressDialog.mGlobalOutsideCancelable) }
    }

    override fun hideProgressBar() {
        getProgressBar()?.hide()
    }

    override fun showProgressBar() {
        if (isFinishing) return
        getProgressBar()?.setCancelableOnTouchOutside(false)?.show()
    }

    override fun showProgressBar(desc: String, isCancel: Boolean) {
        getProgressBar()?.setDesc(desc)?.setCancelable(isCancel)?.show()
    }

    override fun <T> bindLifecycle(): LifecycleTransformer<T> {
        return this.bindUntilEvent(ActivityEvent.DESTROY)
    }

    override fun <T> bindUIThread(): UIThreadTransformer<T> {
        return UIThreadTransformer()
    }

    override fun <T> bindProgress(): ProgressTransformer<T> {
        return ProgressTransformer(getProgressBar())
    }

    override fun <T> bindProgress(bindDialog: Boolean): ProgressTransformer<T> {
        return ProgressTransformer(getProgressBar(), bindDialog)
    }

    open fun <T : Activity> getActivityNew(): T? = fragmentActivity as T

    /**
     * 拦截返回事件
     */
    fun setInterceptBackEvent(interceptBackEvent: Boolean, interceptCallBack: (() -> Unit)? = null) {
        this.interceptBackEvent = interceptBackEvent
        this.interceptCallBack = interceptCallBack
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            interceptCallBack?.invoke()
            if (interceptBackEvent) false else super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }

    fun setAutoHideKeyboard(value: Boolean){
        this.autoHideSoftInput = value
    }

    fun setDispatchTouchEventLisenter(callBack: ((ev: MotionEvent?) -> Unit)? = null){
        this.dispatchTouchEventCallBack = callBack
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(autoHideSoftInput){
            if(ev?.action == MotionEvent.ACTION_DOWN){
                val focusView = currentFocus
                if(focusView is EditText){
                    if(isViewOutside(focusView, ev)){
                        //当前触摸位置不处于焦点控件中，需要隐藏软键盘
                        KeyboardUtils.hideSoftInput(focusView)
                        focusView.clearFocus()
                    }
                }
            }
        }
        dispatchTouchEventCallBack?.invoke(ev)
        return super.dispatchTouchEvent(ev)
    }

    fun isViewOutside(view: View, event: MotionEvent?): Boolean{
        if(event == null) return false
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val left = location[0]
        val top = location[1]
        val bottom = top + view.height
        val right = left + view.width
        return !(event.x > left
                && event.x < right
                && event.y > top
                && event.y < bottom)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressBar()
        dispatchTouchEventCallBack = null
        interceptCallBack = null
        stateLayouts.clear()
        if(this is IViewBinding<*>){
            viewbindingDestory()
        }
    }
}