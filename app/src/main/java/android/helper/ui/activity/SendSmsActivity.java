package android.helper.ui.activity;

import android.annotation.SuppressLint;
import android.helper.R;
import android.helper.adapters.SmsAdapter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.httpclient.RetrofitHelper;
import com.android.helper.utils.SpUtil;
import com.android.helper.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendSmsActivity extends BaseTitleActivity {

    private String KEY_SAVE = "phone_number_save";
    private List<String> mListAddress = new ArrayList<>();
    private List<String> mListResult = new ArrayList<>();
    private SmsAdapter smsAdapter1;
    private SmsAdapter smsAdapter2;
    private RecyclerView rv_address_list;
    private RecyclerView rv_result_list;
    private int interval = 60;
    private EditText et_add_address;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_send_sms;
    }

    @Override
    protected void initView() {
        super.initView();

        setTitleContent("短信轰炸");

        et_add_address = findViewById(R.id.et_add_address);

        rv_address_list = findViewById(R.id.rv_address_list);
        rv_result_list = findViewById(R.id.rv_result_list);

        TextView btn_start_send = findViewById(R.id.btn_start_send);
        TextView btn_stop_send = findViewById(R.id.btn_stop_send);
        TextView btn_add_address = findViewById(R.id.btn_add_address);
        TextView btn_clear_addrss = findViewById(R.id.btn_clear_addrss);

        setonClickListener(btn_add_address);

        btn_start_send.setOnClickListener(v -> mHandler.sendEmptyMessage(123456));

        btn_stop_send.setOnClickListener(v -> {
            mHandler.removeMessages(1234565);
            mHandler.removeCallbacksAndMessages(null);
        });

        btn_clear_addrss.setOnClickListener(v -> {
            boolean b = SpUtil.clearMap(KEY_SAVE);
            if (b) {
                ToastUtil.show("清空成功！");
                // 刷新adapter
                mListAddress.clear();
                smsAdapter1.setList(mListAddress);
            } else {
                ToastUtil.show("清空失败！");
            }
        });

        // 清空输入数据
        findViewById(R.id.tv_clear_input).setOnClickListener(v -> et_add_address.setText(""));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_add_address:
                String address = et_add_address.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.show("添加的发送地址为空！");
                    return;
                }

                mListAddress.add(address);
                SpUtil.putMap(KEY_SAVE, address, address);

                // 刷新adapter
                smsAdapter1.setList(mListAddress);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        smsAdapter1 = new SmsAdapter(mContext, 1);

        HashMap<String, String> map = SpUtil.getMap(KEY_SAVE);

        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();
                mListAddress.add(value);
            }
        }

        // 设置地址
        smsAdapter1.setList(mListAddress);
        rv_address_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv_address_list.setAdapter(smsAdapter1);

        // 设置结果
        smsAdapter2 = new SmsAdapter(mContext, 2);
        smsAdapter2.setList(mListResult);
        rv_result_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv_result_list.setAdapter(smsAdapter2);
    }

    public void sendSms(String url) {
        try {
            OkHttpClient timeOutClient = RetrofitHelper.getTimeOutClient();
            Request.Builder builder = new Request
                    .Builder()
                    .url(url);
            Call call = timeOutClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    rv_result_list.post(new Runnable() {
                        @Override
                        public void run() {
                            mListResult.add("发送失败~");
                            smsAdapter2.setList(mListResult);
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    rv_result_list.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                mListResult.add("发送成功~");
                            } else {
                                mListResult.add("发送失败~");
                            }

                            smsAdapter2.setList(mListResult);
                        }
                    });
                }
            });
        } catch (Exception e) {
            ToastUtil.show(e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListAddress.size() > 0) {
                for (int i = 0; i < mListAddress.size(); i++) {
                    String s = mListAddress.get(i);
                    sendSms(s);

                    if (i == mListAddress.size() - 1) {
                        // 最后一个再次发送
                        mHandler.sendEmptyMessageDelayed(123456, interval * 1000);
                    }
                }
            } else {
                ToastUtil.show("发送地址为空");
            }
        }
    };

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(1234565);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
