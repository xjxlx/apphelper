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

    public List<Fragment> mListFragment;
    public List<String> mListTitles;

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mListFragment = fragments;
        mListTitles = titles;
    }

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mListFragment = fragments;
    }

    public void setList(List<Fragment> fragments, List<String> titles) {
        mListFragment = fragments;
        mListTitles = titles;
        notifyDataSetChanged();
    }

    public void setList(List<Fragment> fragments) {
        mListFragment = fragments;
        setList(fragments, null);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if ((mListFragment != null) && (position < mListFragment.size())) {
            fragment = mListFragment.get(position);
        }
        return fragment;
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
