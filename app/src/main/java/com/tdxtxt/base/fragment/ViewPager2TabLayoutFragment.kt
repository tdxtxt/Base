package com.tdxtxt.base.fragment

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tdxtxt.base.R
import com.tdxtxt.baselib.adapter.viewpager.ViewPager2Adapter
import com.tdxtxt.baselib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_tablayout_viewpager2.*
import java.util.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/5
 *     desc   :
 * </pre>
 */
class ViewPager2TabLayoutFragment : BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_tablayout_viewpager2

    override fun initUi() {
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("推荐", TestFragment()))
        fragments.add(Pair("创新与变革", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))

        val adapter = ViewPager2Adapter(fragmentActivity, fragments)
        viewPager.adapter = adapter
        tabLayout.setViewPager2(viewPager, adapter.getPageTitles())
    }

}