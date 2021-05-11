package android.helper.test;

import android.helper.R;

import com.android.helper.base.BaseTitleActivity;

public class SlidingMenuActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_sliding_menu;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("侧滑的View");
    }
}