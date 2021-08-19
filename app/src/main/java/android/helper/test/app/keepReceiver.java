package android.helper.test.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.helper.utils.LogUtil;

public class keepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // 开始锁屏
        if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
            LogUtil.e("关闭了屏幕");
            KeepManager.getInstance().startKeep(context);

        } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
            // 开始开屏
            LogUtil.e("打开了屏幕");
            KeepManager.getInstance().finishKeep();
        }
    }
}