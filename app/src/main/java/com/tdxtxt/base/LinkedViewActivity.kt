package com.tdxtxt.base

import android.util.Pair
import androidx.fragment.app.Fragment
import com.tdxtxt.base.fragment.TestFragment
import com.tdxtxt.baselib.adapter.viewpager.ViewPagerAdapter
import com.tdxtxt.baselib.tools.StatusBarHelper
import com.tdxtxt.baselib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_linked.*
import java.util.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/4/14
 *     desc   :
 * </pre>
 */
class LinkedViewActivity : BaseActivity() {
    override fun getLayoutResId() = R.layout.activity_linked

    override fun initStatusBar() {
        StatusBarHelper.setLightMode(this)
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

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        tabLayout.setViewPager(viewPager)
    }


}