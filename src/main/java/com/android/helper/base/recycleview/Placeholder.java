package com.android.helper.base.recycleview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.helper.R;

/**
 * @author : 流星
 * @CreateDate: 2021/11/10-4:56 下午
 * @Description: 项目占位图的工具类
 * <ol>
 *     使用说明：使用该占位图工具，可以给页面设置空页面的信息 和 异常页面的信息
 *     使用方法：
 *          一：普通设置
 *              1：设置空数据的图片和文字，调用方法{@link Builder#setEmpty(int, String)}
 *              2：设置空数据提示内容的颜色，调用{@link Builder#setEmptyContentColor(int)}
 *              3：设置空数据提示内容的大小，调用方法{@link Builder#setEmptyContentSize(int)}
 * <p>
 *              4：设置错误的图片，调用方法{@link Builder#setErrorImage(int)}
 *              5：设置错误的标题，调用方法{@link Builder#setErrorTitle(String)}
 *              6：设置错误的按钮内容，调用方法{@link Builder#setErrorButtonContent(String)}
 *              7：设置错误的按钮背景，调用方法{@link Builder#setErrorButtonBackground(int)}
 * <p>
 *          二：全局异常资源设置
 *                 说明：考虑到异常页面的占位图不会随意的变化，所以设置了一套静态的资源文件，供全局使用，一旦设置了新的异常资源，就不会使用全局的异常资源
 * <p>
 *              1：设置全局的异常资源，调用方法{@link Placeholder#setGlobalPlaceholder(Placeholder)} }
 *              2：获取全局的异常资源，调用方法{@link Placeholder#getGlobalPlaceholder()}
 * <p>
 *          三：获取资源
 *                  说明：文档只写了各种设置方法，具体的获取方式，如果是全局的，去使用静态的对应方法获取，如果不是全局的，就使用普通对象去获取，和设置方法都是一一对应的，设置是setXX( )方法，
 *                  获取就是getXX()方法
 * </ol>
 */
public class Placeholder {

    private View mEmptyView;                            // 空数据的指定布局

    private int mEmptyResource;                         // 空布局指定的图片资源
    private String mEmptyContent;                       // 空布局指定的文字内容
    private int mEmptyTitleSize;                        // 空布局文字的大小
    private int mEmptyTitleColor;                       // 空布局文字的颜色
    private int mTypeForView;                           // view的来源,1:指定的view ，2：默认的view

    private int mErrorImage;                            // 错误布局指定的图片资源
    private String mErrorTitle;                         // 错误的提示
    private int mErrorTitleSize;                        // 空布局文字的大小
    private int mErrorTitleColor;                       // 空布局文字的颜色
    private int mErrorButtonBackground;                 // 错误布局的按钮背景
    private String mErrorButtonContent;                 // 错误布局的按钮文字
    private boolean isFromGlobal;                       // 数据是否是全局设置的

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
            this.mEmptyView = builder.mEmptyView;
            this.mTypeForView = builder.mTypeForView;
            this.mEmptyResource = builder.mEmptyResource;
            this.mEmptyContent = builder.mEmptyContent;
            this.mEmptyTitleSize = builder.mEmptyTitleSize;
            this.mEmptyTitleColor = builder.mEmptyTitleColor;

            this.mErrorImage = builder.mErrorImage;
            this.mErrorTitle = builder.mErrorTitle;
            this.mErrorTitleSize = builder.mErrorTitleSize;
            this.mErrorTitleColor = builder.mErrorTitleColor;
            this.mErrorButtonBackground = builder.mErrorButtonBackground;
            this.mErrorButtonContent = builder.mErrorButtonContent;
            this.isFromGlobal = builder.isFromGlobal;
        }
    }

    public View getEmptyView(ViewGroup parent) {
        // 如果没有手动设置空布局，则使用默认的空布局
        if (mTypeForView != 1) {
            // 此处的布局不能作为公共使用，每一个布局，都要使用一个单独的父布局，不然会报错
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

    public int getEmptyContentSize() {
        return mEmptyTitleSize;
    }

    public int getEmptyContentColor() {
        return mEmptyTitleColor;
    }

    public int getErrorImage() {
        return mErrorImage;
    }

    public String getErrorContent() {
        return mErrorTitle;
    }

    public int getErrorTitleSize() {
        return mErrorTitleSize;
    }

    public int getErrorTitleColor() {
        return mErrorTitleColor;
    }

    public int getErrorButtonBackground() {
        return mErrorButtonBackground;
    }

    public String getErrorButtonContent() {
        return mErrorButtonContent;
    }

    /**
     * @return true:表示：是手动设置的，false:默认为false，表示不是手动设置的。此标记是为了确定是否是项目设置了全局的对象，不能单纯地使用对象为空去判断
     */
    public boolean isFromGlobal() {
        return isFromGlobal;
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
        private int mEmptyTitleSize;                        // 空布局文字的大小
        private int mEmptyTitleColor;                       // 空布局文字的颜色
        private int mTypeForView;                           // view的来源,1:指定的view ，2：默认的view

        private int mErrorImage;                            // 错误布局指定的图片资源
        private String mErrorTitle;                         // 错误的提示
        private int mErrorTitleSize;                        // 空布局文字的大小
        private int mErrorTitleColor;                       // 空布局文字的颜色
        private int mErrorButtonBackground;                 // 错误布局的按钮背景
        private String mErrorButtonContent;                 // 错误布局的按钮文字
        private boolean isFromGlobal;                       // 数据是否是全局设置的

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
         * @param resources    指定的图片的资源
         * @param emptyContent 指定的内容
         */
        public Builder setEmpty(int resources, String emptyContent) {
            this.mEmptyResource = resources;
            this.mEmptyContent = emptyContent;
            return this;
        }

        public Builder setEmptyContentSize(int contentSize) {
            this.mEmptyTitleSize = contentSize;
            return this;
        }

        public Builder setEmptyContentColor(int contentColor) {
            this.mEmptyTitleColor = contentColor;
            return this;
        }

        /**
         * @param errorImage 错误图片的对象
         */
        public Builder setErrorImage(int errorImage) {
            mErrorImage = errorImage;
            return this;
        }

        /**
         * @param errorTitle 错误的提示
         */
        public Builder setErrorTitle(String errorTitle) {
            mErrorTitle = errorTitle;
            return this;
        }

        public void setErrorTitleSize(int errorTitleSize) {
            mErrorTitleSize = errorTitleSize;
        }

        public void setErrorTitleColor(int errorTitleColor) {
            mErrorTitleColor = errorTitleColor;
        }

        /**
         * @param errorButtonBackground 错误按钮的背景
         */
        public Builder setErrorButtonBackground(int errorButtonBackground) {
            mErrorButtonBackground = errorButtonBackground;
            return this;
        }

        /**
         * @param errorButtonContent 错误布局的文字描述
         */
        public Builder setErrorButtonContent(String errorButtonContent) {
            mErrorButtonContent = errorButtonContent;
            return this;
        }

        public void setFromGlobal(boolean fromGlobal) {
            isFromGlobal = fromGlobal;
        }

        public Placeholder Build() {
            return new Placeholder(this);
        }
    }

}
