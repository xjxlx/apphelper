package android.helper.test.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class App2Service extends Service {
    public App2Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AppNotificationBinder();
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