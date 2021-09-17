package com.android.helper.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.android.helper.interfaces.listener.OnSelectorListener;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ViewPager2 的 Fragment的adapter
 */
public class BaseViewPager2FragmentAdapter extends FragmentStateAdapter {
    public List<Fragment> mListFragment;
    private OnSelectorListener<Fragment> mSelectorListener;
    private FragmentManager mFragmentManager;

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
        this.mFragmentManager = fragmentManager;
        mListFragment = listFragment;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FragmentViewHolder holder, int position, @NonNull @NotNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (mSelectorListener != null) {
            mSelectorListener.onSelector(null, position, mListFragment.get(position));
        }
    }

    public void setSelectorListener(OnSelectorListener<Fragment> selectorListener) {
        this.mSelectorListener = selectorListener;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        LogUtil.e("createFragment");
        return mListFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return mListFragment == null ? 0 : mListFragment.size();
    }

}
