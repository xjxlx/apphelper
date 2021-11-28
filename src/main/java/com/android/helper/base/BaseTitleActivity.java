package com.android.helper.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.TextViewUtil;

/**
 * @author : 流星
 * @CreateDate: 2021/11/29-1:41
 * @Description: 带标题头的Activity
 */
public abstract class BaseTitleActivity extends BaseActivity {

    protected View mInflate;
    protected FrameLayout mFlActivityContent;
    private RelativeLayout mRlBaseTitleRoot;
    private LinearLayout mLlBaseTitleBack;
    private TextView mTvBaseTitleBackTitle;
    private TextView mTvBaseTitle;
    private FrameLayout mFlBaseTitleRightParent;
    private TextView mTvBaseTitleRightTitle;
    protected View.OnClickListener mBackClickListener = null;// 返回的点击事件
    protected View.OnClickListener mRightBackClickListener = null;// 返回的点击事件

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // 带title的Activity
        mInflate = LayoutInflater.from(this).inflate(R.layout.base_title_activity, null, false);

        // 设置布局
        if (mInflate != null) {
            mFlActivityContent = mInflate.findViewById(R.id.fl_activity_content);  // activity的布局
        }

        // 添加实际的activity
        int titleLayout = getTitleLayout();
        if (titleLayout != 0) {
            // 把真实的布局添加到 mFlActivityContent 中去
            LayoutInflater.from(this).inflate(titleLayout, mFlActivityContent, true);

            // 设置布局
            setContentView(mInflate);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        mRlBaseTitleRoot = mInflate.findViewById(R.id.rl_base_title_root);   // 标题的根布局
        mLlBaseTitleBack = mInflate.findViewById(R.id.ll_base_title_back); // 返回的父类布局
        mTvBaseTitleBackTitle = mInflate.findViewById(R.id.tv_base_title_back_title); // 返回的文字
        mTvBaseTitle = mInflate.findViewById(R.id.tv_base_title); // 标题的内容
        mFlBaseTitleRightParent = mInflate.findViewById(R.id.fl_base_title_right_parent); // 右侧标题的父布局
        mTvBaseTitleRightTitle = mInflate.findViewById(R.id.tv_base_title_right_title); // 右侧标题的textView

        // 设置标题
        String titleContent = setTitleContent();
        if (!TextUtils.isEmpty(titleContent)) {
            TextViewUtil.setText(mTvBaseTitle, titleContent);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mLlBaseTitleBack.setOnClickListener(this); // 左侧返回键的点击事件
        mFlBaseTitleRightParent.setOnClickListener(this); // 右侧标题的返回
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
     * @param showTitle 隐藏或者显示title
     */
    protected void showTitle(boolean showTitle) {
        if (mRlBaseTitleRoot != null) {
            mRlBaseTitleRoot.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 右侧的title
     */
    protected void setRightTitle(String rightTitle) {
        if (TextUtils.isEmpty(rightTitle)) {
            return;
        }

        int visibility = mTvBaseTitleRightTitle.getVisibility();
        if (visibility != View.VISIBLE) {
            mTvBaseTitleRightTitle.setVisibility(View.VISIBLE);
        }

        TextViewUtil.setText(mTvBaseTitleRightTitle, rightTitle);
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 标题
     * @param color      颜色
     * @param size       大小
     */
    protected void setRightTitle(String rightTitle, int color, int size) {
        setRightTitle(rightTitle);
        if (color != 0) {
            mTvBaseTitleRightTitle.setTextColor(color);
        }
        if (size > 0) {
            mTvBaseTitleRightTitle.setTextSize(size);
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
     * 页面的返回
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
     * 设置返回按钮的点击事件
     *
     * @param listener 返回的点击事件
     */
    protected void setBackClickListener(View.OnClickListener listener) {
        this.mBackClickListener = listener;
    }

    /**
     * 设置右侧的点击事件
     *
     * @param listener 右侧的点击事件
     */
    protected void setRightTitleClickListener(View.OnClickListener listener) {
        this.mRightBackClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        // 返回键
        int id = v.getId();
        if (id == R.id.ll_base_title_back) {  // 左侧的标题返回
            if (mBackClickListener != null) {
                // 处理额外的点击事件
                mBackClickListener.onClick(v);
            } else {
                // 直接返回
                finish();
            }
        } else if (id == R.id.fl_base_title_right_parent) { // 右侧的标题返回
            if (mRightBackClickListener != null) {
                // 处理额外的点击事件
                mRightBackClickListener.onClick(v);
            } else {
                // 直接返回
                finish();
            }
        }

    }
}
