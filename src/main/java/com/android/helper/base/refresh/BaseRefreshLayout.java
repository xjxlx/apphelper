package com.android.helper.base.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * SmartRefreshLayout 刷新的扩展类，为了避免以后升级的改动，如果改动就全部在这里进行， 尽量避免改动布局
 */
public class BaseRefreshLayout extends SmartRefreshLayout {

    public BaseRefreshLayout(Context context) {
        super(context);
        initView(context,null);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {

    }

}
