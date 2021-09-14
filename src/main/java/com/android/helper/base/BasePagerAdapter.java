package com.android.helper.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 如果使用了这个类型，就必须重写{@link PagerAdapter#instantiateItem(ViewGroup, int)}方法
 *
 * @param <T> 具体的类型
 */
public class BasePagerAdapter<T> extends PagerAdapter {
    public List<T> mList;

    public BasePagerAdapter(List<T> list) {
        mList = list;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    /**
     * 销毁Item方法，以后都这么写
     * viewpager会自动销毁不用的Item，我们在销毁Item的时候也要销毁view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        //  super.destroyItem(container, position, object);
        //:从容器中溢出view
        container.removeView((View) object);
    }

}
