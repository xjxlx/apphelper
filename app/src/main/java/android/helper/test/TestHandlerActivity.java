package android.helper.test;

import android.annotation.SuppressLint;
import android.helper.R;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TestHandlerActivity extends BaseTitleActivity {

    private TextView textView;
    private List<String> mList = new ArrayList<String>();

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_handler;
    }

    @Override
    protected void initData() {
        super.initData();
        setonClickListener(R.id.button, R.id.button2, R.id.button3, R.id.button4);
        textView = findViewById(R.id.textView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.button:
                Message message2 = mHandler.obtainMessage();
                message2.what = 456;
                mHandler.sendMessage(message2);
                break;

            case R.id.button2:
                Message message = mHandler.obtainMessage();
                message.what = 123;
                mHandler.sendMessageDelayed(message, 1000);
                break;

            case R.id.button3:
                mHandler.removeMessages(456);
                break;
            case R.id.button4:
                mHandler.removeMessages(123);
                break;
        }
    }

    int position = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 123:
                    mList.add("123");

                    LogUtil.writeDe("蓝牙数据", "我是测试数据：" + (++position));

                    Message message = mHandler.obtainMessage();
                    message.what = 123;
                    mHandler.sendMessageDelayed(message, 1000);

                    break;
                case 456:
                    mList.add("456");

                    Message message2 = mHandler.obtainMessage();
                    message2.what = 456;
                    mHandler.sendMessageDelayed(message2, 1000);

                    break;
            }

            textView.setText(mList.toString());
        }
    };
}