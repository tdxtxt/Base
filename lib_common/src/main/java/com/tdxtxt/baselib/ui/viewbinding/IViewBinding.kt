package com.tdxtxt.baselib.ui.viewbinding

import android.view.View
import androidx.viewbinding.ViewBinding
import com.tdxtxt.baselib.dialog.BottomBaseDialog
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.baselib.ui.BaseFragment


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/9/16
 *     desc   : viewbinding接口封装，注意使用不当会抛出如下的错误
 * </pre>
 */
interface IViewBinding<T: ViewBinding> {
    fun viewbind(rootView: View): T

    fun setViewBindingRoot(rootView: View) {
        if(this is BaseActivity){
            viewbindingWrapper.viewbinding = viewbind(rootView)
        }else if(this is BaseFragment){
            viewbindingWrapper.viewbinding = viewbind(rootView)
        }else if(this is BottomBaseDialog){
            viewbindingWrapper.viewbinding = viewbind(rootView)
        }else if(this is CenterBaseDialog){
            viewbindingWrapper.viewbinding = viewbind(rootView)
        }else{
            throw Throwable("你的界面必须是BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog的子类")
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
        }else{
            throw Throwable("你的界面必须是BaseActivity、BaseFragment、BottomBaseDialog、CenterBaseDialog的子类")
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
        }
    }
}