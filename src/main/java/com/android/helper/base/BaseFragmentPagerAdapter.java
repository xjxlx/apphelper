package com.android.helper.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * viewPager 的pager的adapter
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mListFragment;
    private List<String> mListTitles;

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mListFragment = fragments;
        mListTitles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return mListFragment.get(position);
    }

    @Override
    public int getCount() {
        return mListFragment == null ? 0 : mListFragment.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if ((mListTitles == null) || (mListTitles.size() <= 0) || (mListTitles.get(position) == null)) {
            return "";
        }
        return mListTitles.get(position);
    }
}
