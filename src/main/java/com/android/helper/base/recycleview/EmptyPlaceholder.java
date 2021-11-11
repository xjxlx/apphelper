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

    private View mEmptyView;                            // 空数据的指定布局
    private int mEmptyResource;                         // 空布局指定的图片资源
    private String mEmptyContent;                       // 空布局指定的文字内容
    private int mContentSize;                           // 空布局文字的大小
    private int mContentColor;                          // 空布局文字的颜色
    private int mTypeForView;                           // view的来源,1:指定的view ，2：默认的view
    private int mErrorImageResource;                    // 错误布局指定的图片资源
    private String mErrorContent;                       // 错误的提示
    private int mErrorButtonBackgroundResource;         // 错误布局的按钮背景
    private String mErrorButtonContent;                 // 错误布局的按钮文字

    private static int mGlobalErrorImage;                                  // 全局的异常图片
    private static String mGlobalErrorTitle;                             // 全局的异常提示
    private static int mGlobalErrorButtonBackgroundResource;               // 全局的异常Button背景
    private static String mGlobalErrorButtonContent;                       // 全局的异常Button的文字描述

    public static String getGlobalErrorTitle() {
        return mGlobalErrorTitle;
    }

    /**
     * @param globalErrorContent 设置全局的异常提示文字
     */
    public static void setGlobalErrorTitle(String globalErrorContent) {
        mGlobalErrorTitle = globalErrorContent;
    }

    public static int getGlobalErrorButtonBackgroundResource() {
        return mGlobalErrorButtonBackgroundResource;
    }

    /**
     * @param globalErrorButtonBackgroundResource 设置全局的异常Button背景
     */
    public static void setGlobalErrorButtonBackgroundResource(int globalErrorButtonBackgroundResource) {
        mGlobalErrorButtonBackgroundResource = globalErrorButtonBackgroundResource;
    }

    public static String getGlobalErrorButtonContent() {
        return mGlobalErrorButtonContent;
    }

    /**
     * @param globalErrorButtonContent 设置全局的异常Button文字
     */
    public static void setGlobalErrorButtonContent(String globalErrorButtonContent) {
        mGlobalErrorButtonContent = globalErrorButtonContent;
    }

    public static int getGlobalErrorImage() {
        return mGlobalErrorImage;
    }

    /**
     * @param mGlobalErrorImage 设置全局的异常资源图片
     */
    public static void setGlobalErrorImage(int mGlobalErrorImage) {
        EmptyPlaceholder.mGlobalErrorImage = mGlobalErrorImage;
    }

    public EmptyPlaceholder(Builder builder) {
        if (builder != null) {
            this.mEmptyView = builder.mEmptyView;
            this.mTypeForView = builder.mTypeForView;
            this.mEmptyResource = builder.mEmptyResource;
            this.mEmptyContent = builder.mEmptyContent;
            this.mContentSize = builder.mContentSize;
            this.mContentColor = builder.mContentColor;
            this.mErrorImageResource = builder.mErrorImageResource;
            this.mErrorContent = builder.mErrorContent;
            this.mErrorButtonBackgroundResource = builder.mErrorButtonBackgroundResource;
            this.mErrorButtonContent = builder.mErrorButtonContent;
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

    public int getErrorImageResource() {
        return mErrorImageResource;
    }

    public String getErrorContent() {
        return mErrorContent;
    }

    public int getErrorButtonBackgroundResource() {
        return mErrorButtonBackgroundResource;
    }

    public String getErrorButtonContent() {
        return mErrorButtonContent;
    }

    /**
     * @return view的来源, 1:指定的view ，2：默认的view
     */
    public int getTypeForView() {
        return mTypeForView;
    }

    public static class Builder {
        private View mEmptyView;                            // 空数据的指定布局
        private int mEmptyResource;                         // 空布局指定的图片资源
        private String mEmptyContent;                       // 空布局指定的文字内容
        private int mContentSize;                           // 空布局文字的大小
        private int mContentColor;                          // 空布局文字的颜色
        private int mTypeForView;                           // view的来源,1:指定的view ，2：默认的view
        private int mErrorImageResource;                    // 错误布局指定的图片资源
        private String mErrorContent;                       // 错误的提示
        private int mErrorButtonBackgroundResource;         // 错误布局的按钮背景
        private String mErrorButtonContent;                    // 错误布局的按钮文字

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

        /**
         * @param errorImageResource 错误图片的对象
         */
        public Builder setErrorImageResource(int errorImageResource) {
            mErrorImageResource = errorImageResource;
            return this;
        }

        /**
         * @param errorContent 错误的提示
         */
        public Builder setErrorContent(String errorContent) {
            mErrorContent = errorContent;
            return this;
        }

        /**
         * @param errorButtonBackgroundResource 错误按钮的背景
         */
        public Builder setErrorButtonBackgroundResource(int errorButtonBackgroundResource) {
            mErrorButtonBackgroundResource = errorButtonBackgroundResource;
            return this;
        }

        /**
         * @param errorButtonContent 错误布局的文字描述
         */
        public Builder setErrorButtonContent(String errorButtonContent) {
            mErrorButtonContent = errorButtonContent;
            return this;
        }

        public EmptyPlaceholder Build() {
            return new EmptyPlaceholder(this);
        }
    }

}
