package android.helper.test.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.helper.R;
import android.helper.app.App;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.ServiceUtil;
import com.android.helper.utils.SystemUtil;
import com.android.helper.utils.dialog.DialogUtil;

public class MainActivity extends BaseActivity {

    private NotificationUtil mNotification;
    private DialogUtil mDialog;
    private NotificationServiceConnection mNotificationConnection;

    private boolean mBindService = false; // 是否已经绑定成功了服务
    private App2Service.AppNotificationBinder mServiceControl;
    private App2Service mAudioService;
    private NotificationManager mNotificationManager;
    private Notification.Builder builder;

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initData() {
        super.initData();

        mDialog = DialogUtil
                .getInstance()
                .setContentView(mContext, R.layout.base_default_dialog)
                .setText(R.id.tv_msg, "如果不打开通知的权限，则无法正常使用通知，是否跳转页面手动打开？")
                .setOnClickListener(R.id.tv_qx, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_qd, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNotification.goToSetNotify(mContext);
                        mDialog.dismiss();
                    }
                });

        mNotificationConnection = new NotificationServiceConnection();

        Intent intent = new Intent(mContext, App2Service.class);

        // 1：启动后台服务
        try {
            ServiceUtil.startService(mContext, intent);
        } catch (Exception e) {
            LogUtil.e("开启服务失败:" + e.getMessage());
        }

        // 2：绑定前台的服务,禁止冲洗请的绑定
        if (!mBindService) {
            mBindService = mContext.bindService(intent, mNotificationConnection, BIND_AUTO_CREATE);
            LogUtil.e("开始绑定服务");
        }
        LogUtil.e("bindService--->后台服务绑定成功：$mBindService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SystemUtil instance = SystemUtil.getInstance(App.getInstance());
            boolean ignoringBatteryOptimizations = instance.isIgnoringBatteryOptimizations();
            if (!ignoringBatteryOptimizations) {
                instance.requestIgnoreBatteryOptimizations();
            }
        }
    }

    private void initNotification() {
        if (mNotification == null) {

            mNotification = NotificationUtil
                    .getInstance(mContext)
                    .setTickerText("测试程序的保活性能")
                    .setContentTitle("消息的标题")
                    .setContentText("测试程序的保活")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setActivity(MainActivity.class)
                    .setService(App2Service.class)
                    .setChannelName("App保活")
                    .setChannelDescription("测试渠道的保活性能")
                    .setNotificationLevel(NotificationCompat.PRIORITY_MAX) // 消息等级
                    .setVibrate(true) // 震动
            ;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mNotification.setChannelImportance(NotificationManager.IMPORTANCE_MAX);  // 渠道等级
            }
            mNotification.createNotification();
            mNotification.startLoopForeground((int) System.currentTimeMillis(), 5000, mAudioService);
        }

        if (mNotification != null) {
            boolean b = mNotification.checkOpenNotify(mContext);
            if (!b) {
                mDialog.show();
            }
        }
    }

    class NotificationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e("-----@@@@@@----> onServiceConnected!");
            if (service instanceof App2Service.AppNotificationBinder) {
                mServiceControl = (App2Service.AppNotificationBinder) service;
                LogUtil.e("onServiceConnected--->服务回调成功：$mServiceControl");

                mAudioService = mServiceControl.getService();

                initNotification();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e("-----@@@@@@----> onServiceDisconnected!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    private void unBindService() {
        if (mBindService) {
            mContext.unbindService(mNotificationConnection);
            mBindService = false;
        }
    }

}