package android.helper.ui.activity.widget;

import android.helper.R;
import android.view.View;
import android.view.ViewGroup;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.interfaces.listener.DialogChangeListener;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.dialog.PopupWindowUtil;

public class NameListActivity extends BaseTitleActivity {

    private View viewById;
    private PopupWindowUtil instance;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_name_list;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("自定义名字检测的列表");

        viewById = findViewById(R.id.rl_root);
        View ssss = findViewById(R.id.tv_sss);
        ssss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.show(mContext, ssss);
            }
        });
        instance = PopupWindowUtil.getInstance(mContext)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setOutsideTouchable(true)
                .setContentView(R.layout.pickerview_options, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPopupWindowChangeListener(new DialogChangeListener() {
                    @Override
                    public void onShow(View view) {
                        LogUtil.e("v:onShow");
                    }

                    @Override
                    public void onDismiss() {
                        LogUtil.e("v:onDismiss");
                    }
                })
                .show(mContext, this.viewById);

        this.viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                View inflate = LayoutInflater.from(mContext).inflate(R.layout.pickerview_options, null);
//                PopupWindow popupWindow = new PopupWindow(inflate, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
////                popupWindow.setContentView(inflate);
//                popupWindow.showAsDropDown(viewById);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (instance != null) {
            if (instance.isShowing()) {
                instance.dismiss();
            }
        }
    }
}