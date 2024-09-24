package com.tdxtxt.base

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment
import com.tdxtxt.base.databinding.ActivityTablayoutXBinding
import com.tdxtxt.base.fragment.HtmlFragment
import com.tdxtxt.base.fragment.RecyclerViewTabLayoutFragment
import com.tdxtxt.base.fragment.ViewPager2TabLayoutFragment
import com.tdxtxt.base.fragment.XTabLayoutFragment
import com.tdxtxt.base.fragment.YTabLayoutFragment
import com.tdxtxt.base.fragment.YTabLayoutFragment2
import com.tdxtxt.baselib.adapter.viewpager.ViewPagerAdapter
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.BaseActivity
import com.tdxtxt.baselib.ui.viewbinding.IViewBinding

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/14
 *     desc   :
 * </pre>
 */
class XTabLayoutActivity : BaseActivity(), IViewBinding<ActivityTablayoutXBinding> {
    override fun getLayoutResId() = R.layout.activity_tablayout_x
    override fun view2Binding(rootView: View): ActivityTablayoutXBinding {
        return ActivityTablayoutXBinding.bind(rootView)
    }
    override fun initStatusBar() {
        StatusBarHelper.setLightMode(this)
    }

    override fun initUi() {
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("显示HTML", HtmlFragment()))
        fragments.add(Pair("RecyclerViewFragment", RecyclerViewTabLayoutFragment()))
        fragments.add(Pair("ViewPage2Fragment", ViewPager2TabLayoutFragment()))
        fragments.add(Pair("YTabLayoutFragment", YTabLayoutFragment()))
        fragments.add(Pair("YTabLayoutFragment2", YTabLayoutFragment2()))
        fragments.add(Pair("XTabLayoutFragment", XTabLayoutFragment()))
//        fragments.add(Pair("职业素养", YTabLayoutFragment()))
//        fragments.add(Pair("产品质量", YTabLayoutFragment()))
//        fragments.add(Pair("财务及风险管理", YTabLayoutFragment()))
//        fragments.add(Pair("党群与综合", YTabLayoutFragment()))
//        fragments.add(Pair("战略规划", YTabLayoutFragment()))
//        fragments.add(Pair("知识与流程", YTabLayoutFragment()))
//        fragments.add(Pair("资本运营", YTabLayoutFragment()))
//        fragments.add(Pair("制造技术", YTabLayoutFragment()))
//        fragments.add(Pair("产品定义", YTabLayoutFragment()))
//        fragments.add(Pair("品牌", YTabLayoutFragment()))
//        fragments.add(Pair("营销", YTabLayoutFragment()))
//        fragments.add(Pair("信息化", YTabLayoutFragment()))
//        fragments.add(Pair("客户经验", YTabLayoutFragment()))
//        fragments.add(Pair("采购", YTabLayoutFragment()))
//        fragments.add(Pair("产品及叔叔管理", YTabLayoutFragment()))
//        fragments.add(Pair("平台及先进技术", YTabLayoutFragment()))
//        fragments.add(Pair("整车及动力产品开发", YTabLayoutFragment()))
//        fragments.add(Pair("其他", YTabLayoutFragment()))
//        fragments.add(Pair("222222222222222222", YTabLayoutFragment()))

        viewbinding().viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewbinding().tabLayout.setViewPager(viewbinding().viewPager)
    }


}