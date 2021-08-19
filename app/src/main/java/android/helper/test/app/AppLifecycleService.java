package android.helper.test.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.helper.R;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.android.helper.utils.DateUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;

public class AppLifecycleService extends Service {
    public static final String FILE_NAME = "AppLifecycle";

    private static Notification mNotification;
    private static NotificationUtil mInstance;
    private static final int CODE_NOTIFICATION = 19900713;
    private static final int CODE_SEND_NOTIFICATION = CODE_NOTIFICATION + 1;
    private static final int CODE_INTERVAL = 10 * 1000;

    public AppLifecycleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String sysac = intent.getStringExtra("sysac");
        if (TextUtils.equals(sysac, "sysac")) {
            LogUtil.writeDe(FILE_NAME, "我是被账号拉活的哦！");
        }

        LogUtil.e("onStartCommand --->");
        mNotification = getNotification();

        if (mNotification != null) {
            startForeground(CODE_NOTIFICATION, mNotification);
        }

        Message message = mHandler.obtainMessage();
        message.what = CODE_SEND_NOTIFICATION;
        mHandler.sendMessage(message);
        return START_REDELIVER_INTENT;
    }

    private Notification getNotification() {
        if (mInstance == null) {
            mInstance = NotificationUtil.getInstance(getApplicationContext());
        }

        if (mNotification == null) {
            mNotification = mInstance
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("App的保活消息")
                    .setContentText("App保活的信息")
                    // .setService(this.getClass())
                    .setActivity(AppLifecycleActivity.class)
                    .setVibrate(false)  // 停止震动
                    .setSound(false)    // 停止发出声音
                    .setChannelImportance(NotificationManagerCompat.IMPORTANCE_HIGH) // 消息的等级
                    .setChannelImportance(NotificationManagerCompat.IMPORTANCE_HIGH) // 渠道的等级
                    .setChannelName("App保活")
                    .setChannelDescription("App保活程序的渠道")
                    .createNotification()
                    .getNotification();
        }
        return mNotification;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            // 避免重复性发送消息
            removeMessages(CODE_SEND_NOTIFICATION);
            removeCallbacksAndMessages(null);

            if (msg.what == CODE_SEND_NOTIFICATION) {
                LogUtil.e("---> 开始发送了消息的轮询！");

                // 写入本地的日志信息
                String currentTimeToString = DateUtil.getCurrentTimeToString("yyyy-MM-dd HH:mm:ss");
                LogUtil.writeDe(FILE_NAME, "当前的时间是：" + currentTimeToString + ",我是服务的轮询日志哦！");

                mNotification = getNotification();

                if (mNotification != null) {
                    mNotification.when = System.currentTimeMillis();
                    startForeground(CODE_NOTIFICATION, mNotification);
                }

                Message message = mHandler.obtainMessage();
                message.what = CODE_SEND_NOTIFICATION;
                sendMessageDelayed(message, CODE_INTERVAL);
            }
        }
    };

}