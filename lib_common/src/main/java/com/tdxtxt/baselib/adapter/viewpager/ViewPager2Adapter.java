package com.tdxtxt.baselib.adapter.viewpager;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter<T extends Fragment> extends FragmentStateAdapter {
    private List<Pair<String, T>> list;
    private List<String> titles;

    public ViewPager2Adapter(FragmentActivity fa, List<Pair<String, T>> list) {
        super(fa);
        this.list = list;

        titles = new ArrayList<>();
        for (Pair<String, T> item : list){
            titles.add(item.first);
        }
    }

    public Fragment getItem(int position) {
        return list.get(position).second;
    }

    public CharSequence getPageTitle(int position) {
        return titles != null ? titles.get(position) : "";
    }

    public List<String> getPageTitles(){
        return titles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position).second;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
