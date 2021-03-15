package android.helper.ui.activity.hmview;

import android.helper.R;
import android.helper.base.BaseTitleActivity;

/**
 * 自定义view的练习
 */
public class HmCustomViewActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_hm_custom_view;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("自定义View的测试");

    }
}