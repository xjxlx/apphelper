package com.android.helper.base.title;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.utils.ViewUtil;

/**
 * @author 流星
 * @CreateDate: 2021/11/29-1:41
 * @Description: 带标题头的Activity
 * <ol>
 *   使用说明：
 *       1：使用配置：
 *             为了方便使用书写代码，不用每次都自己去添加一个title，所有封装了这个页面，使用这个页面的时候，会自动的把title给加入到布局中去，
 *          但是，需要提前去初始化一个{@link TitleBuilder }的配置信息，封装所有的title资源id，以便于去寻找对象，建议是在Application中去配置，
 *          供全局使用。
 * <p>
 *       2：view分层：
 *             大面上分为了上下两层，上面一层是title的布局，下面一层是activity真正使用的布局
 *       3：title布局
 *             ①：title的布局分为三个布局，左侧的是返回的布局，其中包括返回的按钮，和返回的文字说明，点击返回的时候，点击的是整个返回的父布局
 *             ②：中间的是一个title的具体布局的内容，可以手动去设置
 *             ③：右侧的是一个 RelativeLayout 布局，里面包含了一个textView,一般是用来设置设置文字，如果有其他的自定义需求的话，可以隐藏文字布局，
 *                然后给RelativeLayout 添加一个需要的布局，并去具体的设置以及使用。
 *       4：具体的Api设置方法，都在{@link TitleBuilder }的方法中有具体的说明，可以去按需求使用
 */
public abstract class BaseTitleActivity extends BaseActivity {

    private TitleBar mTitleBar;
    private TextView mRightText;
    /**
     * title下面contentView的对象
     */
    protected ViewGroup mContentLayout;
    /**
     * title的资源布局View
     */
    protected View mTitleRootLayout;
    private TextView mLeftBackTextView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // 如果单页面没有设置独立的title信息，就使用公用的title信息
        if (mTitleBar == null) {
            TitleBuilder globalBuilder = TitleBar.getGlobalTitleBarBuilder();
            if (globalBuilder != null) {
                mTitleBar = globalBuilder.build(this);
            }
        }

        if (mTitleBar != null) {
            // 获取title的根布局
            mTitleRootLayout = mTitleBar.getTitleRootLayout();
            // 获取内容布局
            mContentLayout = mTitleBar.getContentLayout();

            // 添加实际的activity
            int titleLayout = getTitleLayout();
            if (titleLayout != 0) {
                // 把真实的布局添加到 mFlActivityContent 中去
                if (mContentLayout != null) {
                    LayoutInflater.from(this).inflate(titleLayout, mContentLayout, true);
                }

                // 设置布局
                if (mTitleRootLayout != null) {
                    setContentView(mTitleRootLayout);
                }
            }

            // 返回的父类布局
            ViewGroup leftBackLayout = mTitleBar.getLeftBackLayout();
            // 左侧返回键的点击事件
            leftBackLayout.setOnClickListener(v -> {
                boolean back = setBackClickListener(v);
                if (back) {
                    finish();
                }
            });

            // 返回的文字，此处应该是不会随意变化的，如果有变化了，可以再次拓展
            mLeftBackTextView = mTitleBar.getLeftBackTextView();
            // 设置可见性
            boolean leftBackTextShow = mTitleBar.getLeftBackTextShow();
            ViewUtil.setViewVisible(mLeftBackTextView, leftBackTextShow);

            // 设置返回的文字
            String leftBackText = mTitleBar.getLeftBackText();
            if (!TextUtils.isEmpty(leftBackText)) {
                TextViewUtil.setText(mLeftBackTextView, leftBackText);
            }

            // 右侧标题的父布局
            ViewGroup rightLayout = mTitleBar.getRightLayout();
            // 右侧布局可见性
            boolean rightLayoutShow = mTitleBar.getRightLayoutShow();
            ViewUtil.setViewVisible(rightLayout, rightLayoutShow);

            // 右侧文字描述
            if (rightLayoutShow) {
                // 右侧标题的textView
                mRightText = mTitleBar.getRightTextView();
                boolean rightTextShow = mTitleBar.getRightTextShow();
                ViewUtil.setViewVisible(mRightText, rightTextShow);

                // 右侧的文字点击事件
                if (rightTextShow && mRightText != null) {
                    mRightText.setOnClickListener(this::setRightTitleClickListener);
                }
            }

            // 设置标题
            String titleContent = setTitleContent();
            if (!TextUtils.isEmpty(titleContent)) {
                TextView title = mTitleBar.getTitleView(); // 标题的内容
                TextViewUtil.setText(title, titleContent);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

    }

    @Override
    protected int getBaseLayout() {
        return 0;
    }

    /**
     * @return 获取布局资源
     */
    protected abstract int getTitleLayout();

    /**
     * @return 设置标题内容
     */
    protected abstract String setTitleContent();

    /**
     * @param leftContent 设置左侧返回的文字
     */
    protected void setBackText(String leftContent) {
        if ((mLeftBackTextView != null) && (!TextUtils.isEmpty(leftContent))) {
            TextViewUtil.setText(mLeftBackTextView, leftContent);
        }
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 右侧的title
     */
    protected boolean setRightTitle(String rightTitle) {
        boolean success = false;
        if (TextUtils.isEmpty(rightTitle)) {
            return false;
        } else {
            if (mRightText != null) {
                // 设置可见
                ViewUtil.setViewVisible(mRightText, true);
                // 设置内容
                TextViewUtil.setText(mRightText, rightTitle);
                success = true;
            }
        }
        return success;
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 标题
     * @param color      颜色 ,必须是指定的Color 资源，不能是int资源，例如：R.color.xxx,应该是：ContextCompat.getColor(xxx)
     * @param size       大小，文字的大小，默认是sp的单位
     */
    protected void setRightTitle(String rightTitle, @ColorInt int color, int size) {
        boolean success = setRightTitle(rightTitle);
        if (success) {
            if (color != 0) {
                mRightText.setTextColor(color);
            }

            if (size > 0) {
                mRightText.setTextSize(size);
            }
        }
    }

    /**
     * 页面的返回
     *
     * @param backId    返回的id
     * @param listeners 返回的点击事件
     */
    public void setTitleBack(int backId, View.OnClickListener listeners) {
        if (backId != 0) {
            if (listeners != null) {
                findViewById(backId).setOnClickListener(listeners);
            } else {
                setTitleBack(backId);
            }
        }
    }

    /**
     * 指定id的单纯页面返回
     *
     * @param backId 返回的id
     */
    public void setTitleBack(int backId) {
        if (backId != 0) {
            findViewById(backId).setOnClickListener(v -> finish());
        }
    }

    /**
     * 设置标题
     *
     * @param titleId      标题控件的id
     * @param titleContent 标题的内容
     */
    public void setTitleContent(int titleId, String titleContent) {
        if (titleId != 0) {
            View view = findViewById(titleId);
            if (view instanceof TextView) {
                TextView titleView = (TextView) view;
                TextViewUtil.setText(titleView, titleContent);
            }
        }
    }

    /**
     * @param view 返回的父布局
     * @return 返回true, 可以直接结束页面，false:只相应事件，不结束页面，默认可以结束页面
     */
    protected boolean setBackClickListener(View view) {
        return true;
    }

    /**
     * 设置右侧的点击事件
     */
    protected void setRightTitleClickListener(View view) {
    }

    /**
     * @param titleBar 单个页面指定的titleBar的信息
     */
    public void setTitleBar(TitleBar titleBar) {
        this.mTitleBar = titleBar;
    }

}
