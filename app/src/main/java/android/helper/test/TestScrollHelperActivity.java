package android.helper.test;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.LogUtil;
import android.view.View;

/**
 * 测试滑动工具的帮助类
 */
public class TestScrollHelperActivity extends BaseTitleActivity {

    private android.widget.ImageView mIvHead;
    private android.view.View mVReadView;
    private android.view.View mVBlueView;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_scroll_helper;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试滑动工具的帮助类");

        mIvHead = findViewById(R.id.iv_head);
        mVReadView = findViewById(R.id.v_read_view);
        mVBlueView = findViewById(R.id.v_blue_view);

        int measureSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.AT_MOST);

        mIvHead.measure(0, 0);

        int measuredWidth = mIvHead.getMeasuredWidth();
        int measuredHeight = mIvHead.getMeasuredHeight();

        LogUtil.e("measuredWidth:" + measuredWidth + "   measuredHeight：" + measuredHeight);

    }
}