package android.helper.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.bean.LiveDataModel;
import android.helper.databinding.ActivityLiveDataBinding;
import android.helper.services.MyService1;
import android.helper.services.MyService2;
import android.helper.utils.LogUtil;
import android.helper.utils.ScreenUtil;
import android.helper.utils.ServiceUtil;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class LiveDataTitleActivity extends BaseTitleActivity {

    private ActivityLiveDataBinding binding;
    private LiveDataModel model;
    private Intent intent1;
    private Intent intent2;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_live_data;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("LiveData");
        binding = ActivityLiveDataBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        super.initListener();

        setonClickListener(R.id.bt_ui, R.id.bt_thread, R.id.btn_stop_server1, R.id.btn_stop_server2);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_ui:
                String anotherName = "John Doe";
                model.getName().setValue(anotherName);

                break;
            case R.id.bt_thread:
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String anotherName = "John Doe2";
                        model.getName().postValue(anotherName);
                    }
                };
                thread.start();

                break;

            case R.id.btn_stop_server1:
                stopService(intent1);

                boolean myService1 = ServiceUtil.isServiceRunning(mContext, MyService1.class);
                boolean myService2 = ServiceUtil.isServiceRunning(mContext, MyService2.class);
                LogUtil.e("myService1:" + myService1 + "   ---> myService2:" + myService2);
                break;

            case R.id.btn_stop_server2:
                stopService(intent2);

                boolean myService3 = ServiceUtil.isServiceRunning(mContext, MyService1.class);
                boolean myService4 = ServiceUtil.isServiceRunning(mContext, MyService2.class);
                LogUtil.e("myService1:" + myService3 + "   ---> myService2:" + myService4);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1231231231) {
                mHandler.removeMessages(1231231231);

                ScreenUtil screenUtil = new ScreenUtil();
                screenUtil.unScreenKey(mContext);

                Message message = mHandler.obtainMessage();
                message.what = 1231231231;
                mHandler.sendMessageDelayed(message, 1000);
            }
        }
    };

}