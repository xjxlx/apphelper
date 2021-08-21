package android.helper.test.app;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.helper.R;
import android.helper.test.app.account.AccountHelper;
import android.helper.test.app.keep.KeepManager;
import android.os.Build;

import androidx.fragment.app.FragmentActivity;

import com.android.helper.utils.ActivityUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.RxPermissionsUtil;
import com.android.helper.utils.ServiceUtil;
import com.android.helper.utils.SystemUtil;
import com.android.helper.utils.dialog.DialogUtil;

/**
 * 保活方案的管理器
 */
public class LifecycleManager {

    private static LifecycleManager mLifecycleManager;

    // private static LifecycleListener mLifecycleListener;
    private NotificationUtil mNotificationUtil;
    private DialogUtil mDialogUtil;
    private SystemUtil mSystemUtil;
    private Intent mIntentService;

    public static LifecycleManager getInstance() {
        if (mLifecycleManager == null) {
            mLifecycleManager = new LifecycleManager();
        }
        return mLifecycleManager;
    }

    /**
     * 开启保活的方案，这个方法建议在activity的onCreate方法中卡其
     *
     * @param application 系统级的Context对象
     */
    public void startLifecycle(Application application) {
        if (application != null) {
            // 1:屏幕一像素保活，适用于8.0以下的手机
            KeepManager.getInstance().registerKeep(application);

            // 2：账号保活，适用于所有的手机
            AccountHelper.addAccount(application);//添加账户
            AccountHelper.autoSync(application);//调用告知系统自动同步

            // 3: 启动后台进程，变更为前台进程，通过notification去保活
            mIntentService = new Intent(application, AppLifecycleService.class);
            ServiceUtil.startService(application, mIntentService);

            // 4：通过JobService 去进行系统的轮询处理
            AppJobService.startJob(application);
        }
    }

    /**
     * 停止保活的程序，关闭此方法建议慎用，最好是在onDestroy中去调用，页面正常结束了，说明已经不在需要保活了，可以主动去关闭保活程序
     *
     * @param context 上下文对象
     */
    public void stopLifecycle(Context context) {
        // 1：解除一个像素activity的注册
        try {
            if (context != null) {
                KeepManager.getInstance().unregisterKeep(context);
            }
        } catch (Exception e) {
            LogUtil.e("解除一个像素的页面注册的异常：" + e.getMessage());
        }

        // 2: 停止后台的服务
        try {
            if ((mIntentService != null) && (context != null)) {
                context.stopService(mIntentService);
            }
        } catch (Exception e) {
            LogUtil.e("解除一个像素的页面注册的异常：" + e.getMessage());
        }

        // 3: 停止JobService的服务
        AppJobService.cancel();
    }

    /**
     * 检测notification的权限， startActivityForResult的请求码为{NotificationUtil.CODE_REQUEST_ACTIVITY_NOTIFICATION}
     */
    public void checkNotificationPermissions(Activity activity) {
        if (activity != null) {
            if (mNotificationUtil == null) {
                mNotificationUtil = NotificationUtil.getInstance(activity);
            }
            // 检测是否已经打开了notification
            boolean openNotify = mNotificationUtil.checkOpenNotify(activity);
            if (!openNotify) {
                mDialogUtil = DialogUtil
                        .getInstance()
                        .setContentView(activity, R.layout.base_default_dialog)
                        .setText(R.id.tv_msg, "如果不打开通知的权限，则无法正常使用通知，是否跳转页面手动打开？")
                        .setOnClickListener(R.id.tv_qx, "取消", v -> mDialogUtil.dismiss())
                        .setOnClickListener(R.id.tv_qd, "确定", v -> {
                            mNotificationUtil.goToSetNotify(activity);
                            mDialogUtil.dismiss();
                        });
            }
        }
    }

    /**
     * 检测sd卡的读取权限
     */
    public void checkSdPermissions(FragmentActivity activity) {
        if (activity != null) {
            // 请求app的读写权限
            RxPermissionsUtil util = new RxPermissionsUtil(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            util.setAllPermissionListener((havePermission, permission) -> {
                LogUtil.e("SD卡的读写权限：" + havePermission);
            });
        }
    }

    /**
     * 检测电池优化的权限，这个权限只有在android6.0之后才会去执行，低版本的手机也不用去考虑了，版本过低的话，也不会杀进程那么快的
     */
    private void checkBatteryPermissions(Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 判断电池时候被优化了
                if (mSystemUtil == null) {
                    mSystemUtil = SystemUtil.getInstance(activity.getApplication());
                }
                boolean ignoringBatteryOptimizations = mSystemUtil.isIgnoringBatteryOptimizations();
                if (!ignoringBatteryOptimizations) {
                    mDialogUtil = DialogUtil
                            .getInstance()
                            .setContentView(activity, R.layout.base_default_dialog)
                            .setText(R.id.tv_msg, "请禁止电池优化功能，否则为了保持电量的消耗，会主动杀死App,无法进行系统的保活，是否禁止电池的优化？")
                            .setOnClickListener(R.id.tv_qx, "取消", v -> mDialogUtil.dismiss())
                            .setOnClickListener(R.id.tv_qd, "确定", v -> {
                                // 申请打开电池优化
                                mSystemUtil.requestIgnoreBatteryOptimizations(activity);
                                mDialogUtil.dismiss();
                            });
                    mDialogUtil.show();
                }
            } else {
                LogUtil.e("当前android的版本低于6.0，暂时不参与电池优化的请求");
            }
        }
    }

    /**
     * 检测自动启动的权限
     */
    public void checkAutoStartupPermissions(Activity activity) {
        if (activity != null) {
            mDialogUtil = DialogUtil
                    .getInstance()
                    .setContentView(activity, R.layout.base_default_dialog)
                    .setText(R.id.tv_msg, "为了减少后台运行的时候，系统主动杀死App，请手动打开自动启动的权限，是否打开自动启动的权限？")
                    .setOnClickListener(R.id.tv_qx, "取消", v -> mDialogUtil.dismiss())
                    .setOnClickListener(R.id.tv_qd, "确定", v -> {
                        // 申请打开电池优化
                        mDialogUtil.dismiss();
                        ActivityUtil.toSecureManager(activity);
                    });
            mDialogUtil.show();
        }
    }

    /**
     * 设置数据监听
     */
    public void setLifecycleListener(LifecycleListener lifecycleListener) {
        if (lifecycleListener != null) {
            AppLifecycleService.setLifecycleListener(lifecycleListener);
        }
    }

}
