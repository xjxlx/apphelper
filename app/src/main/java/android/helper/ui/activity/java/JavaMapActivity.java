package android.helper.ui.activity.java;

import android.annotation.SuppressLint;
import android.view.View;

import android.helper.R;
import android.helper.base.BaseTitleActivity;

public class JavaMapActivity extends BaseTitleActivity {
    
    @Override
    protected int getTitleLayout() {
        return R.layout.activity_java_map;
    }
    
    @Override
    protected void initView() {
        super.initView();
        setTitleContent("Java类型的集合类");
    }
    
    @Override
    protected void initListener() {
        super.initListener();
        setonClickListener(R.id.tv_java_life);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_java_life:
                startActivity(TestJavaLifeActivity.class);
                break;
        }
    }

}