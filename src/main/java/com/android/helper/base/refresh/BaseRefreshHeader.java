package com.android.helper.base.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.header.ClassicsHeader;

public class BaseRefreshHeader extends ClassicsHeader {

    public BaseRefreshHeader(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {

    }
}
