package com.tdxtxt.baselib.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.tdxtxt.baselib.dialog.impl.ProgressDialog
import com.juexiao.widget.viewstate.StateLayout
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.rx.transformer.ProgressTransformer
import com.tdxtxt.baselib.rx.transformer.UIThreadTransformer
import com.tdxtxt.baselib.tools.DialogMethodExt
import com.tdxtxt.baselib.tools.StatusBarHelper
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
    protected var interceptBackEvent = false
    protected var interceptCallBack: (() -> Unit)? = null
    private val stateLayouts = SparseArray<StateLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = this
        parseParams(intent) //解析参数
        setContentView(getLayoutResId())
        initStatusBar()
        initUi()
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
    fun getStateView(resId: Int) : StateLayout?{
        var stateLayout: StateLayout? = stateLayouts.get(resId)

        if(stateLayout != null) return stateLayout

        val view: View = findViewById(resId)?: (findViewById<View>(android.R.id.content).apply { resId == android.R.id.content })

        stateLayout = StateLayout(fragmentActivity!!)

        initConfigStateView(view, stateLayout)
        stateLayout.showContent()
        stateLayouts.put(resId, stateLayout)
        return stateLayout
    }

    private fun initConfigStateView(view: View, stateLayout: StateLayout){
        stateLayout.configAll(
                emptyText = "别看了，这里什么都没有",
                loadingLayoutId = R.layout.baselib_statelayout_loading, //自定义加载中布局
                errorLayoutId = R.layout.baselib_statelayout_error, //自定义加载失败布局
                emptyLayoutId = R.layout.baselib_statelayout_empty, //自定义数据位为空的布局
                useContentBgWhenLoading = true, //加载过程中是否使用内容的背景
                retryAutoLoading = true,
//                enableLoadingShadow = true, //加载过程中是否启用半透明阴影盖在内容上面
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
        StatusBarHelper.setStatusBar(this, Color.WHITE)
    }

    override fun getProgressBar(): ProgressDialog? {
        if (mProgressDialog == null) mProgressDialog = DialogMethodExt.createProgressDialog(this, "正在加载...", true)
        return mProgressDialog?.setDesc("正在加载...")?.apply { setCancelable(true) }
    }

    override fun hideProgressBar() {
        getProgressBar()?.hide()
    }

    override fun showProgressBar() {
        if (isFinishing) return
        getProgressBar()?.setCancelableOnTouchOutside(false)?.show()
    }

    override fun showProgressBar(desc: String, isCancel: Boolean) {
        getProgressBar()?.setDesc(desc)?.setCancelable(isCancel)?.show();
    }

    override fun <T> bindLifecycle(): LifecycleTransformer<T> {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
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
    fun setInterceptBackEvent(
            interceptBackEvent: Boolean,
            interceptCallBack: (() -> Unit)? = null
    ) {
        this.interceptBackEvent = interceptBackEvent
        this.interceptCallBack = interceptCallBack
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            interceptCallBack?.invoke()
            if (interceptBackEvent) false else super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        hideProgressBar()
    }
}