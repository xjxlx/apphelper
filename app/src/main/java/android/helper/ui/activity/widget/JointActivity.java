package android.helper.ui.activity.widget;

import android.helper.R;
import android.widget.ImageView;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.photo.GlideUtil;

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

        String url = "http://file.jollyeng.com/picture_book/201903/1553490074.png";

        ImageView left = findViewById(R.id.iv_left);
        ImageView iv_right = findViewById(R.id.iv_right);

        GlideUtil.loadView(mContext, url, left, R.drawable.abc);
        GlideUtil.loadView(mContext, url, iv_right, R.drawable.abc);
    }

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_joint_view;
    }
}
