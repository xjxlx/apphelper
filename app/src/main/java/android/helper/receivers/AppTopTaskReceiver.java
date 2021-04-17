package android.helper.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.helper.utils.LogUtil;

import java.util.Objects;

public class AppTopTaskReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "com.bqxny.zhgj.app.info.task.top")) {
            // ToastUtil.showToast("接收到了发送的广播！");
            LogUtil.e("接收到了发送的广播！");
            Toast.makeText(context, "接收到了发送的广播", Toast.LENGTH_SHORT).show();

            if (context instanceof Activity) {
                Activity context1 = (Activity) context;
                unLockScreen(context1);
            }

//            Intent intent2 = new Intent(context, UnLockActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            String packageName = intent.getStringExtra("packageName");
//            intent2.putExtra("packageName", packageName);
//            context.startActivity(intent2);
        }
    }

    private void unLockScreen(Activity activity) {

        final Window win = activity.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }
}