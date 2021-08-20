package android.helper.test.app.account;

import static android.helper.test.app.AppLifecycleActivity.FILE_NAME;
import static android.helper.test.app.AppLifecycleService.KEY_LIFECYCLE_ACCOUNT;
import static android.helper.test.app.AppLifecycleService.KEY_LIFECYCLE_TYPE;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.helper.test.app.AppJobService;
import android.helper.test.app.AppLifecycleService;
import android.os.Bundle;
import android.os.IBinder;

import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ServiceUtil;

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
            LogUtil.writeDe(FILE_NAME, "账号开始同步，数据开始更新！");

            Context context = getContext().getApplicationContext();
            if (context != null) {
                /*启动服务  --- 主应用 */
                boolean serviceRunning = ServiceUtil.isServiceRunning(context, AppLifecycleService.class);
                if (!serviceRunning) {
                    Intent intent = new Intent(context, AppLifecycleService.class);
                    intent.putExtra(KEY_LIFECYCLE_TYPE, KEY_LIFECYCLE_ACCOUNT);
                    ServiceUtil.startService(context, intent);

                    LogUtil.writeDe(FILE_NAME, "检测到后台服务被杀死了，账号同步的时候主动去拉起后台服务！");
                }

                /*启动服务 --- JobService*/
                boolean jobServiceRunning = ServiceUtil.isServiceRunning(getContext(), AppJobService.class);
                if (!jobServiceRunning) {
                    LogUtil.writeDe(FILE_NAME, "检测到JobService被杀死了，账号同步的时候主动去拉起JobService！");
                    AppJobService.startJob(context);
                }
            }
        }
    }
}