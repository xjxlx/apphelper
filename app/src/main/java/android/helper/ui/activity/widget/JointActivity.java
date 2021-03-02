package android.helper.ui.activity.widget;

import android.view.View;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.ClickUtil;
import android.helper.utils.ToastUtil;

public class JointActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_joint_view;
    }

    @Override
    protected void initData() {
        super.initData();

        setTitleContent("自定义拼接的view");

        View viewById = findViewById(R.id.iv_yl);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.isDoubleClick(1500)) {
                    ToastUtil.show("1111");
                }
            }
        });
    }
}
