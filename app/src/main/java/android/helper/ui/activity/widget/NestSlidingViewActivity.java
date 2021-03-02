package android.helper.ui.activity.widget;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.widget.NestSlidingView;
import android.view.View;

public class NestSlidingViewActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_nest_sliding_view;
    }

    @Override
    protected void initView() {
        super.initView();
        View btnTest = findViewById(R.id.btn_test);
        View rlMiddle = findViewById(R.id.rl_middle);
        NestSlidingView nsv_layout = findViewById(R.id.nsv_layout);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rlMiddle.scrollTo(0, -50);
                nsv_layout.testRefesh();

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        setTitleContent("嵌套的滑动View");
    }

}