package com.android.helper.widget.banner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.helper.base.BaseFragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Banner的fragment的适配器
 */
public class BannerFragmentAdapter extends BaseFragmentPagerAdapter {

    public BannerFragmentAdapter(@NonNull @NotNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, fragments);
    }

    @Override
    public int getCount() {
        if ((mListFragment != null) && (mListFragment.size() > 0)) {
            return mListFragment.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (mListFragment != null && (mListFragment.size() > 0)) {
            // position = position % mListFragment.size();
            return mListFragment.get(position);
        }
        return null;
    }
}
