package com.tdxtxt.base.fragment

import android.util.Pair
import androidx.fragment.app.Fragment
import com.tdxtxt.base.R
import com.tdxtxt.baselib.adapter.viewpager.ViewPage2FixAdapter
import com.tdxtxt.baselib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_tablayout_recycler.*
import java.util.*


/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/11
 *     desc   :
 * </pre>
 */
class RecyclerViewTabLayoutFragment : BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_tablayout_recycler

    override fun initUi() {
        val fragments: MutableList<Pair<String, Fragment>> = ArrayList()
        fragments.add(Pair("推荐", TestFragment()))
        fragments.add(Pair("创新与变革", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))
        fragments.add(Pair("职业素养", TestFragment()))

        val adapter = ViewPage2FixAdapter(fragmentActivity, fragments)
        recyclerView.adapter = adapter
        tabLayout.setRecyclerView(recyclerView, adapter.pageTitles)

    }
}
