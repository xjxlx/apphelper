package android.helper.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import android.helper.utils.LogUtil;
import android.helper.utils.ServiceUtil;

public class MyService2 extends Service {
    public MyService2() {
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("MyService2:--->onStartCommand");
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.what = 19900713;
            mHandler.sendMessage(message);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        boolean serviceRunning = ServiceUtil.isServiceRunning(getBaseContext(), MyService1.class);
        if (!serviceRunning) {
            Intent intent = new Intent(getBaseContext(), MyService1.class);
            startService(intent);
        }
        
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        
        boolean serviceRunning1 = ServiceUtil.isServiceRunning(getBaseContext(), "com.xjx.apphelper");
        
        LogUtil.e("MyService2:--->onDestroy ---> 主线程是否存活：" + serviceRunning1 + " 二线程是否存活：" + serviceRunning);
    }
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.removeMessages(19900713);
            if (msg.what == 19900713) {
                Log.e("MyService2", "我是MyService2，我在持续运行中！");
            }
            Message message = mHandler.obtainMessage();
            message.what = 19900713;
            mHandler.sendMessageDelayed(message, 1000);
        }
    };
}