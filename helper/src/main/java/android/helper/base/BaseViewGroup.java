package android.helper.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * ViewGroup的基类，以后的view都要去集成他去实现，避免重复性的造轮子
 */
public abstract class BaseViewGroup extends ViewGroup {

    public BaseViewGroup(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
    }

}
