package com.tdxtxt.baselib.ui

import com.tdxtxt.baselib.dialog.impl.ProgressDialog
import com.tdxtxt.baselib.rx.transformer.ProgressTransformer
import com.tdxtxt.baselib.rx.transformer.UIThreadTransformer
import com.trello.rxlifecycle3.LifecycleTransformer

interface IView {
    fun getProgressBar(): ProgressDialog?
    fun hideProgressBar()
    fun showProgressBar()
    fun showProgressBar(desc: String, isCancel: Boolean)

    fun <T> bindLifecycle(): LifecycleTransformer<T>
    fun <T> bindUIThread(): UIThreadTransformer<T>
    fun <T> bindProgress(): ProgressTransformer<T>
    fun <T> bindProgress(bindDialog: Boolean): ProgressTransformer<T>
}