package com.android.helper.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ViewPager2 的 Fragment的adapter
 */
public class BaseViewPager2FragmentAdapter extends FragmentStateAdapter {
    public List<Fragment> mListFragment;

    public BaseViewPager2FragmentAdapter(@NonNull @NotNull FragmentActivity fragmentActivity, List<Fragment> listFragment) {
        super(fragmentActivity);
        mListFragment = listFragment;
    }

    public BaseViewPager2FragmentAdapter(@NonNull @NotNull Fragment fragment, List<Fragment> listFragment) {
        super(fragment);
        mListFragment = listFragment;
    }

    public BaseViewPager2FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle, List<Fragment> listFragment) {
        super(fragmentManager, lifecycle);
        mListFragment = listFragment;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return mListFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return mListFragment == null ? 0 : mListFragment.size();
    }
}
