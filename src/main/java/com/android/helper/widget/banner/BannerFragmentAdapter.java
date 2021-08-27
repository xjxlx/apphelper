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
}
