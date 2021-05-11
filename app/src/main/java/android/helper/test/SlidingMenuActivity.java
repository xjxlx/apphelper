package android.helper.test;

import android.helper.R;
import android.view.View;
import android.view.ViewGroup;

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

        View mTvTest = findViewById(R.id.tv_test);

        mTvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = mTvTest.getLayoutParams();
                layoutParams.width = 400;
                mTvTest.setLayoutParams(layoutParams);
            }
        });
    }
}