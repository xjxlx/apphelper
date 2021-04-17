package android.helper.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.helper.utils.LogUtil;
import com.android.helper.utils.SpUtil;

import java.util.Calendar;
import java.util.List;

public class LookDogService extends Service {
    private UsageStats usageStatsResult = null;
    private UsageStatsManager usageStatsManager;
    private long endTime;
    private long startTime;
    private boolean aBoolean;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        usageStatsManager = (UsageStatsManager) getBaseContext().getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_WEEK, -2);
        startTime = calendar.getTimeInMillis();

        Message message = mHandler.obtainMessage();
        message.what = 123;
        mHandler.sendMessage(message);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//
//    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 123) {
                return;
            }
            mHandler.removeCallbacksAndMessages(null);

            List<UsageStats> usageStatses = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStatses != null && usageStatses.size() > 0) {
                for (UsageStats usageStats : usageStatses) {
                    if (usageStatsResult == null || usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                        usageStatsResult = usageStats;
                    }
                }
            }
            if (usageStatsResult != null) {
                String packageName = usageStatsResult.getPackageName();
                aBoolean = SpUtil.getBoolean(packageName);
                LogUtil.e("当前操作的应用是   当前的栈顶应用为：" + "----" + "------------" + packageName + "  选中的状态为：" + aBoolean);
                if (aBoolean) {
                    Intent intent = new Intent();
                    intent.setAction("com.bqxny.zhgj.app.info.task.top");
                    intent.putExtra("packageName", packageName);
                    intent.setComponent(new ComponentName(getPackageName(), "com.xjx.apphelper.receivers.AppTopTaskReceiver"));
                    sendBroadcast(intent);
                    LogUtil.e("发送了广播！");
                }
            }
            Message message = mHandler.obtainMessage();
            message.what = 123;
            mHandler.sendMessageDelayed(message, 1000);
        }
    };

}