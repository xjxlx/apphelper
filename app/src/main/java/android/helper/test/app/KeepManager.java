package android.helper.test.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

public class KeepManager {

    private android.helper.test.app.keepReceiver keepReceiver;
    private WeakReference<Activity> weakReference;
    private static KeepManager keepManager;

    public static KeepManager getInstance() {
        if (keepManager == null) {
            keepManager = new KeepManager();
        }
        return keepManager;
    }

    public void registerKeep(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        keepReceiver = new keepReceiver();
        context.registerReceiver(keepReceiver, intentFilter);
    }

    public void unregisterKeep(Context context) {
        if (keepReceiver != null) {
            context.unregisterReceiver(keepReceiver);
        }
    }

    public void startKeep(Context context) {
        Intent intent = new Intent(context, KeepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    public void finishKeep() {
        if (weakReference != null) {
            Activity activity = weakReference.get();
            if (activity != null) {
                activity.finish();
            }
            weakReference = null;
        }
    }

    public void setKeep(KeepActivity keep) {
        weakReference = new WeakReference<>(keep);
    }

}
