package android.helper.ui.activity.widget;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.LogUtil;

public class CustomTestActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_custom_test;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitleContent("自定义文字");

        float a = 43.1f;
        int round = Math.round(a);
        LogUtil.e("a --->" + round);
        float B = 43.9f;
        int round2 = Math.round(B);
        LogUtil.e("B --->" + round2);
    }
}