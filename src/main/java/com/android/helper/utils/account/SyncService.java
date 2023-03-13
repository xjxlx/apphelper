package com.android.helper.utils.account;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import com.android.helper.common.CommonConstants;
import com.android.helper.utils.LogUtil;

/**
 * 用于执行账户同步，当系统执行账户同步时则会自动拉活所在的进程,不需要手动配置好之后，系统会自动绑定并调起，必须注册
 * <service android:name=".test.app.account.SyncService" android:enabled="true" android:exported="true"> <intent-filter>
 * <action android:name="android.content.SyncAdapter" /> </intent-filter>
 * <p>
 * <meta-data android:name="android.content.SyncAdapter" android:resource="@xml/sync_adapter" /> </service>
 */
public class SyncService extends Service {

    private SyncAdapter syncAdapter;

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapter(getApplicationContext(), true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("------> onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    public static class SyncAdapter extends AbstractThreadedSyncAdapter {
        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            //与互联网 或者 本地数据库同步账户
            LogUtil.e("onPerformSync ---> 开始了账户的同步！" + account.toString());
            LogUtil.writeAll(CommonConstants.FILE_LIFECYCLE_NAME, "应用保活：", "账号开始同步，数据开始更新！");

            // 1:意图都是通过Intent发送的，首先要新建一个Intent
            Intent intent = new Intent();
            // 2:设置一个动作，为了让对方能知道是谁发出的，要做一个标记
            intent.setAction("com.android.helper.lifecycle");
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setClassName(getContext(), "com.android.helper.utils.account.LifecycleReceiver");
            // 3:发送广播
            getContext().sendBroadcast(intent);
        }
    }
}