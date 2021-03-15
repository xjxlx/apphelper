package android.helper.ui.activity.widget;

import android.annotation.SuppressLint;
import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.databinding.ActivityViewMapBinding;
import android.helper.ui.activity.hmview.HmCustomViewActivity;
import android.view.View;

public class ViewMapTitleActivity extends BaseTitleActivity {

    private ActivityViewMapBinding binding;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_view_map;
    }

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityViewMapBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        super.initListener();

        setonClickListener(
                R.id.tv_progress,
                R.id.tv_multiple_list_view, R.id.tv_custom_text, R.id.tv_custom_round,
                R.id.tv_custom_random, R.id.tv_custom_left_and_right, R.id.tv_custom_progress,
                R.id.tv_custom_touch, R.id.tv_custom_input_password, R.id.tv_scroll_view,
                R.id.tv_page_view, R.id.tv_test_hm
        );
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_progress:
                startActivity(ProgressTitleActivity.class);
                break;
            case R.id.tv_multiple_list_view:
                startActivity(MultipleListViewActivity.class);
                break;
            case R.id.tv_custom_text:
                startActivity(CustomTestActivity.class);
                break;
            case R.id.tv_custom_round:
                startActivity(CustomRoundImageActivity.class);
                break;
            case R.id.tv_custom_random:
                startActivity(RandomActivity.class);
                break;
            case R.id.tv_custom_left_and_right:
                startActivity(JointActivity.class);
                break;
            case R.id.tv_custom_progress:
                startActivity(ProgressActivity.class);
                break;
            case R.id.tv_custom_touch:
                startActivity(TouchUnlockActivity.class);
                break;
            case R.id.tv_custom_input_password:
                startActivity(InputPassWordActivity.class);
                break;

            case R.id.tv_scroll_view:
                startActivity(NestSlidingViewActivity.class);
                break;
            case R.id.tv_page_view:
                startActivity(ScrollPageViewActivity.class);
                break;

            case R.id.tv_test_hm:  // 自定义黑马的view
                startActivity(HmCustomViewActivity.class);
                break;
        }
    }

}

