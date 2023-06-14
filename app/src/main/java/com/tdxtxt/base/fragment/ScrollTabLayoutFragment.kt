package com.tdxtxt.base.fragment

import com.tdxtxt.base.R
import com.tdxtxt.baselib.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_tablayout_scroll.*

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/6/9
 *     desc   :
 * </pre>
 */
class ScrollTabLayoutFragment : BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_tablayout_scroll

    override fun initUi() {
        tabLayout.setScrollView(scrollView, listOf(
            Pair(tv_1.text.toString(), tv_1),
            Pair(tv_2.text.toString(), tv_2),
            Pair(tv_3.text.toString(), tv_3),
            Pair(tv_4.text.toString(), tv_4),
            Pair(tv_5.text.toString(), tv_5),
            Pair(tv_6.text.toString(), tv_6)
        ))
    }
}