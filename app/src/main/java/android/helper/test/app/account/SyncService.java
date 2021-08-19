package android.helper.test.app.account;

import static android.helper.test.app.AppLifecycleActivity.FILE_NAME;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import com.android.helper.utils.LogUtil;

/**
 * 用于执行账户同步，当系统执行账户同步时则会自动拉活所在的进程,不需要手动配置好之后，系统会自动绑定并调起
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
            LogUtil.writeDe(FILE_NAME, "我是账号同步时候主动拉活的应用，就是这么的拽呀！");
        }
    }
}