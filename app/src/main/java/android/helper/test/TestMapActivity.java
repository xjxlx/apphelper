package android.helper.test;

import android.annotation.SuppressLint;
import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.view.View;

/**
 * 测试功能的集合
 */
public class TestMapActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_map;
    }

    @Override
    protected void initListener() {
        super.initListener();
        setonClickListener(R.id.tv_test_handler, R.id.tv_elv, R.id.tv_flex_box, R.id.tv_test_js_to_android);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_test_handler:
                startActivity(TestHandlerActivity.class);
                break;
            case R.id.tv_elv:
                startActivity(ExpandableActivity.class);
                break;
            case R.id.tv_flex_box:
                startActivity(FlexBoxLayoutActivity.class);
                break;
            case R.id.tv_test_js_to_android:
                startActivity(AndroidJSActivity.class);
                break;
        }
    }

}