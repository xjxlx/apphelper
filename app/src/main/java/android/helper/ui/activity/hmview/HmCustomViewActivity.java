package android.helper.ui.activity.hmview;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.ToastUtil;

/**
 * 自定义view的练习
 */
public class HmCustomViewActivity extends BaseTitleActivity {

    private android.helper.widget.hm.SwitchView sv;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_hm_custom_view;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("自定义View的测试");

        sv = findViewById(R.id.sv);

        sv.setSwitchChangeListener(isOpen -> ToastUtil.show("当前的状态是：" + isOpen));
    }
}