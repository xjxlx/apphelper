package com.android.helper.base.recycleview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.helper.R;

/**
 * @author : 流星
 * @CreateDate: 2021/11/10-4:56 下午
 * @Description: 项目占位图的工具类 ""
 */
public class Placeholder {
    private ViewGroup mRootView;

    private int mRootViewId;                           // 数据的根布局
    private int mMessageViewId;                        // 中间消息按钮的view
    private int mBottomOtherViewId;                    // 消息按钮下方的额外view，常用于添加一些按钮的操作，需要的话，可以去自己添加
    private int mRefreshViewId;                        // 底部刷新按钮的对象

    // 列表类型空布局
    private int mListEmptyResource;                     // List空布局指定的图片资源
    private String mListEmptyContent;                   // List空布局指定的文字内容
    private float mListEmptyTitleSize;                  // List空布局文字的大小
    private int mListEmptyTitleColor;                   // List空布局文字的颜色

    // page类型空数据
    private String mPageEmptyContent;                    // page类型的空布局站位图提示内容
    private int mPageEmptyResource;                      // page类型的空布局站位图资源
    private float mPageEmptyTitleSize;                   // page空布局文字的大小
    private int mPageEmptyTitleColor;                    // page空布局文字的颜色

    // 公用错误类型数据
    private String mErrorContent;                        // 公用错误类型的空布局站位图提示内容
    private int mErrorResource;                          // 公用错误类型的空布局站位图资源
    private float mErrorTitleSize;                       // 公用错误布局文字的大小
    private int mErrorTitleColor;                        // 公用错误布局文字的颜色
    private String mErrorButtonContent;                  // 公用错误布局的按钮文字

    // 公用无网类型数据
    private int mNoNetWorkImage;                         // 断网的图片
    private String mNoNetWorkContent;                    // 错误的提示
    private float mNoNetWorkTitleSize;                   // 空布局文字的大小
    private int mNoNetWorkTitleColor;                    // 空布局文字的颜色
    private String mNoNetWorkButtonContent;              // 错误布局的按钮文字

    private boolean mShowPlaceHolder;                    // 是否自动显示占位图，默认为true

    @SuppressLint("StaticFieldLeak")
    private static Placeholder GlobalPlaceholder; // 静态的对象

    public static Placeholder getGlobalPlaceholder() {
        return GlobalPlaceholder;
    }

    public static void setGlobalPlaceholder(Placeholder globalPlaceholder) {
        GlobalPlaceholder = globalPlaceholder;
    }

    public Placeholder(Builder builder) {
        if (builder != null) {
            this.mRootViewId = builder.mRootViewId;
            this.mMessageViewId = builder.mMessageViewId;
            this.mBottomOtherViewId = builder.mBottomOtherViewId;
            this.mRefreshViewId = builder.mRefreshViewId;
            this.mListEmptyResource = builder.mListEmptyResource;
            this.mListEmptyContent = builder.mListEmptyContent;
            this.mListEmptyTitleSize = builder.mListEmptyTitleSize;
            this.mListEmptyTitleColor = builder.mListEmptyTitleColor;
            this.mPageEmptyContent = builder.mPageEmptyContent;
            this.mPageEmptyResource = builder.mPageEmptyResource;
            this.mPageEmptyTitleSize = builder.mPageEmptyTitleSize;
            this.mPageEmptyTitleColor = builder.mPageEmptyTitleColor;
            this.mErrorContent = builder.mErrorContent;
            this.mErrorResource = builder.mErrorResource;
            this.mErrorTitleSize = builder.mErrorTitleSize;
            this.mErrorTitleColor = builder.mErrorTitleColor;
            this.mErrorButtonContent = builder.mErrorButtonContent;
            this.mNoNetWorkContent = builder.mNoNetWorkContent;
            this.mNoNetWorkImage = builder.mNoNetWorkImage;
            this.mNoNetWorkTitleSize = builder.mNoNetWorkTitleSize;
            this.mNoNetWorkTitleColor = builder.mNoNetWorkTitleColor;
            this.mNoNetWorkButtonContent = builder.mNoNetWorkButtonContent;
            this.mShowPlaceHolder = builder.mShowPlaceHolder;
        }
    }

    /**
     * @param parent 依赖的父布局
     * @return 获取根布局
     */
    public View getRootView(ViewGroup parent) {
        if (mRootViewId != 0) {
            mRootView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mRootViewId, parent, false);
        } else {
            mRootView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.base_recycleview_empty, parent, false);
        }
        return mRootView;
    }

    /**
     * @return 获取提示消息的view
     */
    public ImageView getPlaceHolderView() {
        ImageView placeHolder = null;
        if (mRootView != null) {
            if (mMessageViewId != 0) {
                placeHolder = mRootView.findViewById(mMessageViewId);
            } else {
                placeHolder = mRootView.findViewById(R.id.iv_base_placeholder_image);
            }
        }
        return placeHolder;
    }

    /**
     * @return 获取提示消息的view
     */
    public TextView getMessageView() {
        TextView messageView = null;
        if (mRootView != null) {
            if (mMessageViewId != 0) {
                messageView = mRootView.findViewById(mMessageViewId);
            } else {
                messageView = mRootView.findViewById(R.id.tv_base_placeholder_msg);
            }
        }
        return messageView;
    }

    /**
     * @return 获取消息下方其他view的父布局，用于自己去动态需要的view
     */
    public ViewGroup getBottomOtherView() {
        ViewGroup otherView = null;
        if (mRootView != null) {
            if (mBottomOtherViewId != 0) {
                otherView = mRootView.findViewById(mBottomOtherViewId);
            } else {
                otherView = mRootView.findViewById(R.id.fl_bottom_placeholder);
            }
        }
        return otherView;
    }

    /**
     * @return 获取底部刷新按钮的view, 可以用来自己定义或者重构
     */
    public TextView getRefreshView() {
        TextView refreshView = null;
        if (mRootView != null) {
            if (mRefreshViewId != 0) {
                refreshView = mRootView.findViewById(mRefreshViewId);
            } else {
                refreshView = mRootView.findViewById(R.id.iv_base_error_placeholder);
            }
        }
        return refreshView;
    }

    public int getListEmptyResource() {
        return mListEmptyResource;
    }

    public String getListEmptyContent() {
        return mListEmptyContent;
    }

    public float getListEmptyTitleSize() {
        return mListEmptyTitleSize;
    }

    public int getListEmptyTitleColor() {
        return mListEmptyTitleColor;
    }

    public String getPageEmptyContent() {
        return mPageEmptyContent;
    }

    public int getPageEmptyResource() {
        return mPageEmptyResource;
    }

    public float getPageEmptyTitleSize() {
        return mPageEmptyTitleSize;
    }

    public int getPageEmptyTitleColor() {
        return mPageEmptyTitleColor;
    }

    public String getErrorContent() {
        return mErrorContent;
    }

    public void setErrorContent(String errorContent) {
        this.mErrorContent = errorContent;
    }

    public int getErrorResource() {
        return mErrorResource;
    }

    public float getErrorTitleSize() {
        return mErrorTitleSize;
    }

    public int getErrorTitleColor() {
        return mErrorTitleColor;
    }

    public String getErrorButtonContent() {
        return mErrorButtonContent;
    }

    public int getNoNetWorkImage() {
        return mNoNetWorkImage;
    }

    public String getNoNetWorkContent() {
        return mNoNetWorkContent;
    }

    public float getNoNetWorkTitleSize() {
        return mNoNetWorkTitleSize;
    }

    public int getNoNetWorkTitleColor() {
        return mNoNetWorkTitleColor;
    }

    public String getNoNetWorkButtonContent() {
        return mNoNetWorkButtonContent;
    }

    public boolean isShowPlaceHolder() {
        return mShowPlaceHolder;
    }

    public static class Builder {
        private int mRootViewId;                           // 数据的根布局
        private int mMessageViewId;                        // 中间消息按钮的view
        private int mBottomOtherViewId;                    // 消息按钮下方的额外view，常用于添加一些按钮的操作，需要的话，可以去自己添加
        private int mRefreshViewId;                        // 底部刷新按钮的对象

        // 列表类型空布局
        private int mListEmptyResource;                     // List空布局指定的图片资源
        private String mListEmptyContent;                   // List空布局指定的文字内容
        private float mListEmptyTitleSize;                    // List空布局文字的大小
        private int mListEmptyTitleColor;                   // List空布局文字的颜色

        // page类型空数据
        private String mPageEmptyContent;                    // page类型的空布局站位图提示内容
        private int mPageEmptyResource;                      // page类型的空布局站位图资源
        private float mPageEmptyTitleSize;                     // page空布局文字的大小
        private int mPageEmptyTitleColor;                    // page空布局文字的颜色

        // 公用错误类型数据
        private String mErrorContent;                        // 公用错误类型的空布局站位图提示内容
        private int mErrorResource;                          // 公用错误类型的空布局站位图资源
        private float mErrorTitleSize;                         // 公用错误布局文字的大小
        private int mErrorTitleColor;                        // 公用错误布局文字的颜色
        private String mErrorButtonContent;                  // 公用错误布局的按钮文字

        // 公用无网类型数据
        private int mNoNetWorkImage;                         // 断网的图片
        private String mNoNetWorkContent;                    // 错误的提示
        private float mNoNetWorkTitleSize;                   // 空布局文字的大小
        private int mNoNetWorkTitleColor;                    // 空布局文字的颜色
        private String mNoNetWorkButtonContent;              // 错误布局的按钮文字

        private boolean mShowPlaceHolder;                    // 是否自动显示占位图，默认为true

        public Builder setRootViewId(int rootViewId) {
            mRootViewId = rootViewId;
            return this;
        }

        public Builder setMessageViewId(int messageViewId) {
            mMessageViewId = messageViewId;
            return this;
        }

        public Builder setBottomOtherViewId(int bottomOtherViewId) {
            mBottomOtherViewId = bottomOtherViewId;
            return this;
        }

        public Builder setRefreshViewId(int refreshViewId) {
            mRefreshViewId = refreshViewId;
            return this;
        }

        public Builder setListEmptyResource(int listEmptyResource) {
            mListEmptyResource = listEmptyResource;
            return this;
        }

        public Builder setListEmptyContent(String listEmptyContent) {
            mListEmptyContent = listEmptyContent;
            return this;
        }

        public Builder setListEmptyTitleSize(float listEmptyTitleSize) {
            mListEmptyTitleSize = listEmptyTitleSize;
            return this;
        }

        public Builder setListEmptyTitleColor(int listEmptyTitleColor) {
            mListEmptyTitleColor = listEmptyTitleColor;
            return this;
        }

        public Builder setPageEmptyContent(String pageEmptyContent) {
            mPageEmptyContent = pageEmptyContent;
            return this;
        }

        public Builder setPageEmptyResource(int pageEmptyResource) {
            mPageEmptyResource = pageEmptyResource;
            return this;
        }

        public Builder setPageEmptyTitleSize(float pageEmptyTitleSize) {
            mPageEmptyTitleSize = pageEmptyTitleSize;
            return this;
        }

        public Builder setPageEmptyTitleColor(int pageEmptyTitleColor) {
            mPageEmptyTitleColor = pageEmptyTitleColor;
            return this;
        }

        public Builder setErrorContent(String errorContent) {
            mErrorContent = errorContent;
            return this;
        }

        public Builder setErrorResource(int errorResource) {
            mErrorResource = errorResource;
            return this;
        }

        public Builder setErrorTitleSize(float errorTitleSize) {
            mErrorTitleSize = errorTitleSize;
            return this;
        }

        public Builder setErrorTitleColor(int errorTitleColor) {
            mErrorTitleColor = errorTitleColor;
            return this;
        }

        public Builder setErrorButtonContent(String errorButtonContent) {
            mErrorButtonContent = errorButtonContent;
            return this;
        }

        public Builder setNoNetWorkImage(int noNetWorkImage) {
            mNoNetWorkImage = noNetWorkImage;
            return this;
        }

        public Builder setNoNetWorkContent(String noNetWorkTitle) {
            mNoNetWorkContent = noNetWorkTitle;
            return this;
        }

        public Builder setNoNetWorkTitleSize(float noNetWorkTitleSize) {
            mNoNetWorkTitleSize = noNetWorkTitleSize;
            return this;
        }

        public Builder setNoNetWorkTitleColor(int noNetWorkTitleColor) {
            mNoNetWorkTitleColor = noNetWorkTitleColor;
            return this;
        }

        public Builder setNoNetWorkButtonContent(String noNetWorkButtonContent) {
            mNoNetWorkButtonContent = noNetWorkButtonContent;
            return this;
        }

        public Builder setShowPlaceHolder(boolean showPlaceHolder) {
            mShowPlaceHolder = showPlaceHolder;
            return this;
        }

        public Placeholder Build() {
            return new Placeholder(this);
        }
    }

}
