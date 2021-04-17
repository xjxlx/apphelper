package android.helper.test;

import android.helper.R;

import com.android.helper.base.BaseTitleActivity;

public class MoreAdapterActivity extends BaseTitleActivity {

    private androidx.recyclerview.widget.RecyclerView mRvList;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_more_adapter;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试多布局的类型");

        mRvList = findViewById(R.id.rv_list);
    }

    @Override
    protected void initData() {
        super.initData();

    }
}