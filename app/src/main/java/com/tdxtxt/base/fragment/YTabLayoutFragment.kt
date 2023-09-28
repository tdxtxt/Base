package com.tdxtxt.base.fragment

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment
import com.tdxtxt.base.R
import com.tdxtxt.base.databinding.FragmentTablayoutYBinding
import com.tdxtxt.baselib.adapter.viewpager.ViewPagerAdapter
import com.tdxtxt.baselib.ui.BaseFragment
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/31
 *     desc   :
 * </pre>
 */
class YTabLayoutFragment : BaseFragment(), IViewBinding<FragmentTablayoutYBinding> {
    override fun getLayoutId() = R.layout.fragment_tablayout_y
    override fun view2Binding(rootView: View): FragmentTablayoutYBinding {
        return FragmentTablayoutYBinding.bind(rootView)
    }
    override fun initUi() {
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("推荐", TestFragment()))
        fragments.add(Pair("创新与变革", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))
        fragments.add(Pair("产品质量", TestFragment()))
        fragments.add(Pair("财务及风险管理", TestFragment()))
        fragments.add(Pair("党群与综合", TestFragment()))
        fragments.add(Pair("战略规划", TestFragment()))
        fragments.add(Pair("知识与流程", TestFragment()))
        fragments.add(Pair("资本运营", TestFragment()))
        fragments.add(Pair("制造技术", TestFragment()))
        fragments.add(Pair("产品定义", TestFragment()))
        fragments.add(Pair("品牌", TestFragment()))
        fragments.add(Pair("营销", TestFragment()))
        fragments.add(Pair("信息化", TestFragment()))
        fragments.add(Pair("客户经验", TestFragment()))
        fragments.add(Pair("采购", TestFragment()))
        fragments.add(Pair("产品及叔叔管理", TestFragment()))
        fragments.add(Pair("平台及先进技术", TestFragment()))
        fragments.add(Pair("整车及动力产品开发", TestFragment()))
        fragments.add(Pair("其他", TestFragment()))
        fragments.add(Pair("222222222222222222", TestFragment()))

        viewbinding().viewPager.adapter = ViewPagerAdapter(childFragmentManager, fragments)
        viewbinding().tabLayout.setViewPager(viewbinding().viewPager)
        viewbinding().viewPager.setCurrentItem(22)
    }
}