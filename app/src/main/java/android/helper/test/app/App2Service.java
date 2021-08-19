package android.helper.test.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.android.helper.utils.LogUtil;

public class App2Service extends Service {
    public App2Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AppNotificationBinder();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.e("onStart:");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("onStartCommand:");
        return START_REDELIVER_INTENT;
    }

    class AppNotificationBinder extends Binder implements AppNotificationInterface {
        @Override
        public void startLoop() {

        }

        @Override
        public void stopLoop() {

        }

        @Override
        public App2Service getService() {
            return App2Service.this;
        }
    }
}