package com.android.helper.base;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.helper.R;
import com.android.helper.utils.TextViewUtil;

/**
 * 适用于单纯添加title的activity
 */
public abstract class BaseTitleActivity extends BaseActivity {

    protected LinearLayout mLlBaseTitleBack;
    protected LinearLayout mRlBaseTitleRoot;
    protected ImageView mIvBaseTitleBack;
    protected TextView mTvBaseTitleBackTitle;
    protected TextView mTvBaseTitle;
    protected FrameLayout mFlBaseTitleRightParent;
    protected TextView mTvBaseTitleRightTitle;
    protected FrameLayout mFlBaseTitleContent;
    protected View.OnClickListener mBackClickListener = null;// 返回的点击事件

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_base_title;
    }

    @Override
    public void onBeforeCreateView() {
        super.onBeforeCreateView();
        // root
        mRlBaseTitleRoot = findViewById(R.id.rl_base_title_root);
        // left parent
        mLlBaseTitleBack = findViewById(R.id.ll_base_title_back);
        // lift icon
        mIvBaseTitleBack = findViewById(R.id.iv_base_title_back);
        // left title
        mTvBaseTitleBackTitle = findViewById(R.id.tv_base_title_back_title);
        // title
        mTvBaseTitle = findViewById(R.id.tv_base_title);
        // right parent
        mFlBaseTitleRightParent = findViewById(R.id.fl_base_title_right_parent);
        // right  title
        mTvBaseTitleRightTitle = findViewById(R.id.tv_base_title_right_title);
        // content
        mFlBaseTitleContent = findViewById(R.id.fl_base_title_content);

        // 把继承title的布局添加到title布局中
        LayoutInflater.from(mContext).inflate(getTitleLayout(), mFlBaseTitleContent);

        // 设置返回事件
        setonClickListener(R.id.ll_base_title_back);
    }

    /**
     * @return 获取子类布局的view
     */
    protected abstract int getTitleLayout();

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    /**
     * 给activity设置title
     *
     * @param title 标题内容
     */
    protected void setTitleContent(String title) {
        if (!TextUtils.isEmpty(title)) {
            TextViewUtil.setText(mTvBaseTitle, title);
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
     * 设置右侧的点击事件
     *
     * @param listener 右侧的点击事件
     */
    protected void setRightTitleClickListener(View.OnClickListener listener) {
        if (listener != null) {
            mFlBaseTitleRightParent.setOnClickListener(listener);
        }
    }

    /**
     * @return 获取背景的布局
     */
    protected View getTitleRoot() {
        return mRlBaseTitleRoot;
    }

    /**
     * @return 获取右侧的title
     */
    protected View getRightTitle() {
        return mFlBaseTitleRightParent;
    }

    /**
     * @param showTitle 隐藏或者显示title
     */
    protected void showTitle(boolean showTitle) {
        if (mRlBaseTitleRoot != null) {
            mRlBaseTitleRoot.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();

        // 返回键
        if (id == R.id.ll_base_title_back) {
            if (mBackClickListener != null) {
                // 处理额外的点击事件
                mBackClickListener.onClick(v);
            } else {
                // 直接返回
                finish();
            }
        }
    }
}
