package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.helper.R;

/**
 * @author : 流星
 * @CreateDate: 2021/11/10-4:56 下午
 * @Description:
 */
public class EmptyPlaceholder {

    private View mEmptyView;        // 空数据的指定布局
    private int mEmptyResource;     // 空布局指定的图片资源
    private String mEmptyContent;   // 空布局指定的文字内容
    private int mContentSize;      // 空布局文字的大小
    private int mContentColor;      // 空布局文字的颜色
    private int mTypeForView;          // view的来源,1:指定的view ，2：默认的view

    public EmptyPlaceholder(Builder builder) {
        if (builder != null) {
            this.mEmptyView = builder.mEmptyView;
            this.mTypeForView = builder.mTypeForView;
            if (mTypeForView == 2) { // 这里只给默认的空布局设置资源，否则没有意义
                this.mEmptyResource = builder.mEmptyResource;
                this.mEmptyContent = builder.mEmptyContent;
                this.mContentSize = builder.mContentSize;
                this.mContentColor = builder.mContentColor;
            }
        }
    }

    public View getEmptyView(ViewGroup parent) {
        // 如果没有手动设置空布局，则使用默认的空布局
        if ((mEmptyView == null) && (mTypeForView != 1)) {
            if (parent != null) {
                mEmptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_recycleview_empty, parent, false);
                mTypeForView = 2;
            }
        }
        return mEmptyView;
    }

    public int getEmptyResource() {
        return mEmptyResource;
    }

    public String getEmptyContent() {
        return mEmptyContent;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public int getContentColor() {
        return mContentColor;
    }

    /**
     * @return view的来源, 1:指定的view ，2：默认的view
     */
    public int getTypeForView() {
        return mTypeForView;
    }

    public static class Builder {
        private int mEmptyResource;     // 空布局指定的图片资源
        private String mEmptyContent;   // 空布局指定的文字内容
        private View mEmptyView;        // 空数据的指定布局
        private int mContentSize;      // 空布局文字的大小
        private int mContentColor;      // 空布局文字的颜色
        private int mTypeForView;          // view的来源,1:指定的view ，2：默认的view

        /**
         * 设置空布局的view
         *
         * @param view 指定的空布局的view
         */
        public Builder setEmptyView(View view) {
            this.mEmptyView = view;
            mTypeForView = 1;
            return this;
        }

        /**
         * @param resources 指定的图片的资源
         * @param content   指定的内容
         */
        public Builder setEmpty(int resources, String content) {
            this.mEmptyResource = resources;
            this.mEmptyContent = content;
            return this;
        }

        public Builder setEmptySize(int contentSize) {
            this.mContentSize = contentSize;
            return this;
        }

        public Builder setEmptyColor(int contentColor) {
            this.mContentColor = contentColor;
            return this;
        }

        public EmptyPlaceholder Build() {
            return new EmptyPlaceholder(this);
        }
    }

}
