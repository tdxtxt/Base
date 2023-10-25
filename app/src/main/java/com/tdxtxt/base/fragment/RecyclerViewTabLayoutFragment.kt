package com.tdxtxt.base.fragment

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.FragmentTablayoutRecyclerBinding
import com.tdxtxt.baselib.adapter.viewpager.RecyclerViewFragmentAdapter
import com.tdxtxt.baselib.ui.BaseFragment
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/11
 *     desc   :
 * </pre>
 */
class RecyclerViewTabLayoutFragment : BaseFragment(),
    IViewBinding<FragmentTablayoutRecyclerBinding> {
    override fun getLayoutId() = R.layout.fragment_tablayout_recycler
    override fun view2Binding(rootView: View): FragmentTablayoutRecyclerBinding {
        return FragmentTablayoutRecyclerBinding.bind(rootView)
    }

    override fun initUi() {
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("推荐", TestFragment()))
        fragments.add(Pair("创新与变革", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))

        val adapter =
            RecyclerViewFragmentAdapter(
                fragmentActivity,
                fragments
            )
        viewbinding().recyclerView.adapter = adapter
        viewbinding().tabLayout.setRecyclerView(viewbinding().recyclerView, adapter.pageTitles)

    }


}
