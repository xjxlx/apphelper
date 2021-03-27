package android.helper.test;

import android.helper.R;
import android.helper.base.BaseTitleActivity;

/**
 * 测试滑动工具的帮助类
 */
public class TestScrollHelperActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_scroll_helper;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试滑动工具的帮助类");
    }
}