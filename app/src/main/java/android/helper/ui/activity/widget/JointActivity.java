package android.helper.ui.activity.widget;

import android.helper.R;
import android.helper.base.BaseActivity;
import android.helper.utils.ClickUtil;
import android.helper.utils.ToastUtil;
import android.view.View;

/**
 * 自定义左右的布局
 */
public class JointActivity extends BaseActivity {

    @Override
    protected void initData() {
        super.initData();

//        View viewById = findViewById(R.id.iv_yl);
//        viewById.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int height = viewById.getHeight();
//                int width = viewById.getWidth();
//                if (!ClickUtil.isDoubleClick(1500)) {
//                    ToastUtil.show("width:" + width + "   height:" + height);
//                }
//            }
//        });
    }

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_joint_view;
    }
}
