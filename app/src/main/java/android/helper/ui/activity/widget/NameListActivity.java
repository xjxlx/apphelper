package android.helper.ui.activity.widget;

import android.helper.R;

import com.android.helper.base.BaseTitleActivity;

public class NameListActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_name_list;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("自定义名字检测的列表");
    }
}