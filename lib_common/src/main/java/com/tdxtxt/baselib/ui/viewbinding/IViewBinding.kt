package com.tdxtxt.baselib.ui.viewbinding

import android.app.Dialog
import android.view.View
import androidx.viewbinding.ViewBinding
import com.tdxtxt.baselib.R
import com.tdxtxt.baselib.dialog.BottomBaseDialog
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.baselib.ui.BaseFragment


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/9/16
 *     desc   : 支持的基类：BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog、View、Dialog
 * </pre>
 */
interface IViewBinding<T: ViewBinding> {
    fun view2Binding(rootView: View): T

    /**
     * 基类为 BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog  ➀重写viewbind方法绑定视图
     * 基类为 View、Dialog   ➀创建方法中调用setViewBindingRoot方法；
     *                      ➁重写viewbind方法绑定视图；
     *                      ➂不允许使用setTag(R.id.tag_viewbinding, Any)占用ViewBindingWrapper对象保存的坑位
     */
    fun setViewBindingRoot(rootView: View) {
        if(this is BaseActivity){
            viewbindingWrapper.viewbinding = view2Binding(rootView)
        }else if(this is BaseFragment){
            viewbindingWrapper.viewbinding = view2Binding(rootView)
        }else if(this is BottomBaseDialog){
            viewbindingWrapper.viewbinding = view2Binding(rootView)
        }else if(this is CenterBaseDialog){
            viewbindingWrapper.viewbinding = view2Binding(rootView)
        }else if(this is View){
            if(viewbindingWrapper == null) viewbindingWrapper = ViewBindingWrapper()
            viewbindingWrapper?.viewbinding = view2Binding(rootView)
        }else if(this is Dialog){
            if(viewbindingWrapper == null) viewbindingWrapper = ViewBindingWrapper()
            viewbindingWrapper?.viewbinding = view2Binding(rootView)
        }else{
            throw Throwable("你的界面必须是BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog、View、Dialog的子类")
        }
    }

    fun viewbinding(): T {
        if(this is BaseActivity){
            return viewbindingWrapper.viewbinding as T
        }else if(this is BaseFragment){
            return viewbindingWrapper.viewbinding as T
        }else if(this is BottomBaseDialog){
            return viewbindingWrapper.viewbinding as T
        }else if(this is CenterBaseDialog){
            return viewbindingWrapper.viewbinding as T
        }else if(this is View){
            return viewbindingWrapper?.viewbinding as T
        }else if(this is Dialog){
            return viewbindingWrapper?.viewbinding as T
        }else{
            throw Throwable("你的界面必须是BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog、View、Dialog的子类")
        }
    }

    fun viewbindingOrNull(): T? {
        if(this is BaseActivity){
            return viewbindingWrapper.viewbinding as T?
        }else if(this is BaseFragment){
            return viewbindingWrapper.viewbinding as T?
        }else if(this is BottomBaseDialog){
            return viewbindingWrapper.viewbinding as T?
        }else if(this is CenterBaseDialog){
            return viewbindingWrapper.viewbinding as T?
        }else if(this is View){
            return viewbindingWrapper?.viewbinding as T?
        }else if(this is Dialog){
            return viewbindingWrapper?.viewbinding as T?
        }else{
            throw Throwable("你的界面必须是BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog、View、Dialog的子类")
        }
    }

    fun destory(){
        if(this is BaseActivity){
            viewbindingWrapper.viewbinding = null
        }else if(this is BaseFragment){
            viewbindingWrapper.viewbinding = null
        }else if(this is BottomBaseDialog){
            viewbindingWrapper.viewbinding = null
        }else if(this is CenterBaseDialog){
            viewbindingWrapper.viewbinding = null
        }else if(this is View){
            viewbindingWrapper?.viewbinding = null
        }else if(this is Dialog){
            viewbindingWrapper?.viewbinding = null
        }
    }
}

private var View.viewbindingWrapper: ViewBindingWrapper<ViewBinding>?
    get() = getTag(R.id.tag_viewbinding) as ViewBindingWrapper<ViewBinding>?
    set(value) = setTag(R.id.tag_viewbinding, value)

private var Dialog.viewbindingWrapper: ViewBindingWrapper<ViewBinding>?
    get() = window?.decorView?.getTag(R.id.tag_viewbinding) as ViewBindingWrapper<ViewBinding>?
    set(value) = window?.decorView?.setTag(R.id.tag_viewbinding, value)?: Unit