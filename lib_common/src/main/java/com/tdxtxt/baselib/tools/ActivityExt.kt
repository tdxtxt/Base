package com.tdxtxt.baselib.tools

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicInteger

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/16
 *     desc   :
 * </pre>
 */
object ActivityExt {
    private val nextLocalRequestCode = AtomicInteger()
    @JvmStatic
    fun <I, O> FragmentActivity.startContractForResult(contract: ActivityResultContract<I, O>, input: I, callback: ActivityResultCallback<O>?){
        val key = "act_result${nextLocalRequestCode.getAndIncrement()}"
        val registry = activityResultRegistry
        var launcher: ActivityResultLauncher<I>? = null
        val observer = object : LifecycleEventObserver{
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(Lifecycle.Event.ON_DESTROY == event){
                    launcher?.unregister()
                    lifecycle.removeObserver(this)
                }
            }
        }
        lifecycle.addObserver(observer)
        val newCallback = ActivityResultCallback<O> {
            launcher?.unregister()
            lifecycle.removeObserver(observer)
            callback?.onActivityResult(it)
        }
        launcher = registry.register(key, contract, newCallback)
        launcher.launch(input)
    }

    @JvmStatic
    fun FragmentActivity.startActivityForResult(intent: Intent, callback: ActivityResultCallback<ActivityResult>?){
        startContractForResult(ActivityResultContracts.StartActivityForResult(), intent, callback)
    }
}