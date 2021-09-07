package com.android.helper.base.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.footer.ClassicsFooter;

/**
 * SmartRefreshLayout 刷新脚部的扩展类
 */
public class BaseRefreshFooter extends ClassicsFooter {

    public BaseRefreshFooter(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {

    }

}
