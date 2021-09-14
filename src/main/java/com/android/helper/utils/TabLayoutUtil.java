package com.android.helper.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.helper.R;
import com.google.android.material.tabs.TabLayout;

/**
 * 使用这个工具类，必须要viewPager设置了adapter之后才可以
 */
public class TabLayoutUtil {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Context mContext;
    private Resources mResources;
    private PagerAdapter mAdapter;
    private int mAdapterCount;

    private int mSelectorColor;
    private int mSelectorTextSize;
    private int mSelectorTypeface;
    private int mNormalColor;
    private int mNormalTextSize;
    private int mNormalTypeface;
    private int mCurrentItem;

    public TabLayoutUtil(Builder builder) {
        if (builder != null) {
            this.mSelectorColor = builder.selectorColor;
            this.mSelectorTextSize = builder.selectorTextSize;
            this.mSelectorTypeface = builder.selectorTypeface;
            this.mNormalColor = builder.normalColor;
            this.mNormalTextSize = builder.normalTextSize;
            this.mNormalTypeface = builder.normalTypeface;
            this.mCurrentItem = builder.currentItem;
        }
    }

    /**
     * @param viewPager viewPager的对象
     * @param tabLayout TabLayout的对象
     * @return 关联 ViewPager 和 TabLayout
     */
    public TabLayoutUtil setupWithViewPager(ViewPager viewPager, TabLayout tabLayout) {
        this.mViewPager = viewPager;
        this.mTabLayout = tabLayout;
        if ((mViewPager != null) && (tabLayout != null)) {

            mContext = mViewPager.getContext();
            if (mContext != null) {
                mResources = mContext.getResources();
            }
            mAdapter = mViewPager.getAdapter();
            if (mAdapter != null) {
                mAdapterCount = mAdapter.getCount();
            }

            // 关联
            tabLayout.setupWithViewPager(mViewPager);

            // 添加tab
            addTab();
            // 添加监听器
            addSelectorListener();
            // 设置默认的item
            setDefaultItem();

        }
        return this;
    }

    private void addTab() {
        if (mAdapterCount > 0) {
            // 手动添加自定义的table
            for (int i = 0; i < mAdapterCount; i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(getTabView(i));
                }
            }
        }
    }

    /**
     * 自定义Tab的View
     */
    private View getTabView(int currentPosition) {
        if ((mAdapter != null) && (mContext != null)) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.indicator_tab, null);
            TextView textView = view.findViewById(R.id.tv_indicator);
            if (currentPosition < mAdapterCount) {
                CharSequence pageTitle = mAdapter.getPageTitle(currentPosition);
                TextViewUtil.setText(textView, pageTitle);
            }
            return view;
        }
        return null;
    }

    private void addSelectorListener() {
        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View view = tab.getCustomView();
                    setTextViewSelector(view, true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View view = tab.getCustomView();
                    setTextViewSelector(view, false);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private void setTextViewSelector(View view, boolean selector) {
        if ((view != null) && (mResources != null)) {
            TextView textView = view.findViewWithTag("indicator");
            if (selector) {
                if (mSelectorTextSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mResources.getDimension(mSelectorTextSize));
                }
                if (mSelectorColor > 0) {
                    textView.setTextColor(ContextCompat.getColor(mContext, mSelectorColor));
                }
                try {
                    if (mSelectorTypeface > 0) {
                        textView.setTypeface(Typeface.defaultFromStyle(mSelectorTypeface));
                    }
                } catch (Exception ignored) {
                }
            } else {
                if (mNormalTextSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mResources.getDimension(mNormalTextSize));
                }
                if (mNormalColor > 0) {
                    textView.setTextColor(ContextCompat.getColor(mContext, mNormalColor));
                }
                try {
                    if (mNormalTypeface > 0) {
                        textView.setTypeface(Typeface.defaultFromStyle(mNormalTypeface));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 设置默认的数据
     */
    private void setDefaultItem() {
        if (mViewPager != null) {
            if (mCurrentItem >= 0) {
                mViewPager.setCurrentItem(mCurrentItem);
            }
            TabLayout.Tab tabAt = mTabLayout.getTabAt(mCurrentItem);
            if (tabAt != null) {
                View customView = tabAt.getCustomView();
                setTextViewSelector(customView, true);
            }
        }
    }

    /**
     * 必须是在设置了adapter之后才可以使用哦
     */
    public static class Builder {
        private int selectorColor;
        private int selectorTextSize;
        private int selectorTypeface;

        private int normalColor;
        private int normalTextSize;
        private int normalTypeface;
        private int currentItem;

        public Builder setSelectorColor(int selectorColor) {
            this.selectorColor = selectorColor;
            return this;
        }

        public Builder setSelectorTextSize(int selectorTextSize) {
            this.selectorTextSize = selectorTextSize;
            return this;
        }

        /**
         * @param selectorTypeface {@link Typeface#NORMAL} or  {@link Typeface#BOLD}
         */
        public Builder setSelectorTypeface(int selectorTypeface) {
            this.selectorTypeface = selectorTypeface;
            return this;
        }

        public Builder setNormalColor(int normalColor) {
            this.normalColor = normalColor;
            return this;
        }

        public Builder setNormalTextSize(int normalTextSize) {
            this.normalTextSize = normalTextSize;
            return this;
        }

        /**
         * @param normalTypeface {@link Typeface#NORMAL} or  {@link Typeface#BOLD}
         */
        public Builder setNormalTypeface(int normalTypeface) {
            this.normalTypeface = normalTypeface;
            return this;
        }

        public Builder setCurrentItem(int currentItem) {
            this.currentItem = currentItem;
            return this;
        }

        public TabLayoutUtil Build() {
            return new TabLayoutUtil(this);
        }

    }

}
