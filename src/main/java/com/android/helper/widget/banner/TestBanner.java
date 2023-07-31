package com.android.helper.widget.banner;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.common.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestBanner extends PagerAdapter {
    private List<View> mList;

    public TestBanner(List<View> list) {
        mList = list;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View view = mList.get(position);

        // if (view != null) {
        // ViewParent parent = view.getParent();
        // if (parent != null) {
        // container.removeView(view);
        // }
        // }
        // container.addView(view);

        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent == null) {
                // container.removeView(view);
                container.addView(view);
                LogUtil.e("移除了相同的view");
            }
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container, position, object);
        container.removeView(mList.get(position));
    }

}
