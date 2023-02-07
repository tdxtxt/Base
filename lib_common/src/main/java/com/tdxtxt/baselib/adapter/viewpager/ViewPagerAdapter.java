package com.tdxtxt.baselib.adapter.viewpager;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考资料 Fragment 这些 API 已废弃 https://blog.csdn.net/vitaviva/article/details/125839973
 */
public class ViewPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {
    private List<Pair<String, T>> list;
    private List<String> titles;

    public ViewPagerAdapter(FragmentManager fm, List<Pair<String, T>> list) {
//        super(fm);
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;

        titles = new ArrayList<>();
        for (Pair<String, T> item : list){
            titles.add(item.first);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position).second;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles != null ? titles.get(position) : super.getPageTitle(position);
    }
}
