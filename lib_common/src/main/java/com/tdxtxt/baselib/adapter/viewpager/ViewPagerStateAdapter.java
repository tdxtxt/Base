package com.tdxtxt.baselib.adapter.viewpager;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 适用于多Fragment的情况：参考资料 ViewPager中Fragment状态保存 https://mp.weixin.qq.com/s/C7PsGcwKjMgRTZ7j0cLPfw
 * 保存View状态前提：1、view要设置id；2、view要设置setSaveEnabled(true)，默认为ture；
 * 注意：自定义状态请自行实现view相关的方法onSaveInstanceState、onRestoreInstanceState
 */
public class ViewPagerStateAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    private List<Pair<String, T>> list;
    private List<String> titles;

    public ViewPagerStateAdapter(FragmentManager fm, List<Pair<String, T>> list) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
