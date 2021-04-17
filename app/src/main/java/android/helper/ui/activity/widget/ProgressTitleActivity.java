package android.helper.ui.activity.widget;

import android.helper.R;
import android.helper.databinding.ActivityProgressBinding;

import com.android.helper.base.BaseTitleActivity;

public class ProgressTitleActivity extends BaseTitleActivity {

    private ActivityProgressBinding binding;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_progress;
    }

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
    }
}