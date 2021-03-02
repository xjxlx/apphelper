package android.helper.ui.activity;

import android.content.Intent;
import android.view.View;

import android.helper.R;
import android.helper.databinding.ActivityUnLockBinding;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.SpUtil;

public class UnLockTitleActivity extends BaseTitleActivity {
    
    private ActivityUnLockBinding binding;
    
    @Override
    protected void initView() {
        super.initView();
        binding = ActivityUnLockBinding.inflate(getLayoutInflater());
    }
    
    @Override
    protected void initData() {
        super.initData();
        
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("packageName");
        binding.btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.putBoolean(packageName, false);
                finish();
            }
        });
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        SpUtil.putBoolean("show", false);
    }
    
    @Override
    protected int getTitleLayout() {
        return R.layout.activity_un_lock;
    }
    
}