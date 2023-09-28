package com.tdxtxt.base.fragment

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.FragmentTablayoutViewpager2Binding
import com.tdxtxt.baselib.adapter.viewpager.ViewPager2Adapter
import com.tdxtxt.baselib.ui.BaseFragment
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/5
 *     desc   :
 * </pre>
 */
class ViewPager2TabLayoutFragment : BaseFragment(), IViewBinding<FragmentTablayoutViewpager2Binding> {
    override fun getLayoutId() = R.layout.fragment_tablayout_viewpager2
    override fun view2Binding(rootView: View): FragmentTablayoutViewpager2Binding {
        return FragmentTablayoutViewpager2Binding.bind(rootView)
    }

    override fun initUi() {
        viewbinding().viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("推荐", TestFragment()))
        fragments.add(Pair("创新与变革", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))

        val adapter = ViewPager2Adapter(fragmentActivity, fragments)
        viewbinding().viewPager.adapter = adapter
        viewbinding().tabLayout.setViewPager2(viewbinding().viewPager, adapter.getPageTitles())
    }

}