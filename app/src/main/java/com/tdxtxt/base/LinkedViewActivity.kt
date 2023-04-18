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
        fragments.add(Pair("培训任务", TestFragment()))
        fragments.add(Pair("课程xx", TestFragment()))
        fragments.add(Pair("课程xxxxx", TestFragment()))
        fragments.add(Pair("课程ddddddd", TestFragment()))
        fragments.add(Pair("课程d", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))
        fragments.add(Pair("课程ddd", TestFragment()))
        fragments.add(Pair("课程df", TestFragment()))

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        tabLayout.setViewPager(viewPager)
    }


}