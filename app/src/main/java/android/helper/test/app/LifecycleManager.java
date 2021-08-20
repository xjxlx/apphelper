package android.helper.test.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.helper.test.app.account.AccountHelper;
import android.helper.test.app.keep.KeepManager;

import com.android.helper.utils.ServiceUtil;

/**
 * 保活方案的管理器
 */
public class LifecycleManager {

    private static LifecycleListener mLifecycleListener;

    /**
     * 开启保活的方案，这个方法建议在activity的onCreate方法中卡其
     *
     * @param application 系统级的Context对象
     */
    public static void startLifecycle(Application application) {
        if (application != null) {
            // 1:屏幕一像素保活，适用于8.0以下的手机
            KeepManager.getInstance().registerKeep(application);

            // 2：账号保活，适用于所有的手机
            AccountHelper.addAccount(application);//添加账户
            AccountHelper.autoSync(application);//调用告知系统自动同步

            // 3: 启动后台进程，变更为前台进程，通过notification去保活
            Intent intent = new Intent(application, AppLifecycleService.class);
            ServiceUtil.startService(application, intent);

            // 4：通过JobService 去进行系统的轮询处理
            AppJobService.startJob(application);
        }
    }

    /**
     * 停止保活的程序，关闭此方法建议慎用，最好是在onDestroy中去调用，页面正常结束了，说明已经不在需要保活了，可以主动去关闭保活程序
     *
     * @param context 上下文对象
     */
    public static void stopLifecycle(Context context) {
        if (context != null) {
            KeepManager.getInstance().unregisterKeep(context);
        }
    }

}
