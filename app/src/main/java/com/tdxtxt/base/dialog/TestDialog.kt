package com.tdxtxt.base.dialog

import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.DialogAppTestBinding
import com.tdxtxt.baselib.dialog.CenterBaseDialog
import com.tdxtxt.baselib.dialog.IBDialog
import com.tdxtxt.baselib.image.ImageLoader
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/9/17
 *     desc   :
 * </pre>
 */
class TestDialog(activity: FragmentActivity?) : CenterBaseDialog(activity), IViewBinding<DialogAppTestBinding> {
    override fun getLayoutId() = R.layout.dialog_app_test
    override fun viewbind(rootView: View): DialogAppTestBinding {
        return DialogAppTestBinding.bind(rootView)
    }
    override fun onCreate(dialog: IBDialog) {
        ImageLoader.loadImageCircle(viewbinding().ivImage, "https://n.sinaimg.cn/tech/transform/346/w179h167/20220119/090c-d3cbde60cd5d0eac46025e8c740c9e90.gif", 2f, Color.BLUE)
        viewbinding().tvContent.text = "12343二的犯得上发射点发大水发射点发射点但是发射点发射点发"
    }

}