package android.helper.widget.hm;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;

/**
 * 目标：打造一个能左右滑动的布局
 * 思路：
 * 1：因为是一层压着一层的左右滑动，所以布局继承frameLayout最合适，不用去再继承viewGroup
 * 2：因为要滑动，首先拿到上下两层布局的对象，
 */
public class SlidingMenuLayout extends FrameLayout {

    private final String tag = "------>:SlidingMenu";

    public SlidingMenuLayout(@NonNull Context context) {
        super(context);
        initView(context,null);
    }

    public SlidingMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LogUtil.e(tag, "SlidingMenuLayout");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtil.e(tag, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtil.e(tag, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e(tag, "onDraw");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.e(tag, "onSizeChanged");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.e(tag, "onFinishInflate");
    }

}
