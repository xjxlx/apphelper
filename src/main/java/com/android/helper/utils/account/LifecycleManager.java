package com.android.helper.utils.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.android.helper.R;
import com.android.helper.common.CommonConstants;
import com.android.helper.utils.ActivityUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.ServiceUtil;
import com.android.helper.utils.SpUtil;
import com.android.helper.utils.SystemUtil;
import com.android.helper.utils.account.keep.KeepManager;
import com.android.helper.utils.dialog.DialogUtil;
import com.android.helper.utils.permission.RxPermissionsUtil;

/**
 * 保活方案的管理器
 */
public class LifecycleManager {

    private static LifecycleManager mLifecycleManager;
    private static String mServiceName, mJobServiceName;// 需要启动的服务名字
    private NotificationUtil mNotificationUtil;
    private DialogUtil mDialogUtil;
    private SystemUtil mSystemUtil;
    private Intent mIntent;

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
     * @param serviceName 需要启动服务类的名字
     */
    public void startLifecycle(Context application, String serviceName, String jobName) {
        if ((application != null) && (!TextUtils.isEmpty(serviceName)) && (!TextUtils.isEmpty(jobName))) {
            mServiceName = serviceName;
            mJobServiceName = jobName;

            // 保存名字
            SpUtil.putString(CommonConstants.FILE_LIFECYCLE_SERVICE_NAME, serviceName);
            SpUtil.putString(CommonConstants.FILE_LIFECYCLE_JOB_SERVICE_NAME, jobName);

            // 1:账号保活 todo  排查账号是否已经拉活了
            AccountHelper accountHelper = AccountHelper.getInstance();
            accountHelper
                    .addAccountType(application.getResources().getString(R.string.account_type))
                    .addAccountAuthority(application.getResources().getString(R.string.account_authority))
                    .addAccountName(application.getResources().getString(R.string.account_name))
                    .addAccountPassword(application.getResources().getString(R.string.account_password))
                    .addAccount(application);//添加账户
            accountHelper.autoSync();

            // 2:后台服务写日志
            boolean serviceRunning = ServiceUtil.isServiceRunning(application, serviceName);
            LogUtil.writeLifeCycle("☆☆☆☆☆---我是Manager，当前后台服务的状态为：" + serviceRunning);
            if (!serviceRunning) {
                mIntent = new Intent();
                mIntent.setClassName(application, serviceName);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.putExtra(CommonConstants.KEY_LIFECYCLE_FROM, LifecycleAppEnum.From_Intent.getFrom());
                ServiceUtil.startService(application, mIntent);
            }

            // 3:启动jobService
            boolean jobServiceRunning = ServiceUtil.isJobServiceRunning(application, jobName);
            LogUtil.writeLifeCycle("☆☆☆☆☆---我是Manager，当前JobService的状态为：" + jobServiceRunning);

            if (!jobServiceRunning) {
                AppJobService.startJob(application, LifecycleAppEnum.From_Intent);
            }

            // 4:屏幕一像素保活，适用于8.0以下的手机
            KeepManager.getInstance().registerKeep(application);
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
            if ((mIntent != null) && (context != null)) {
                context.stopService(mIntent);
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
    public void checkNotificationPermissions(FragmentActivity activity) {
        if (activity != null) {
            if (mNotificationUtil == null) {
                mNotificationUtil = new NotificationUtil.Builder(activity).build();
            }
            // 检测是否已经打开了notification
            boolean openNotify = mNotificationUtil.checkOpenNotify(activity);
            if (!openNotify) {
                mDialogUtil = new DialogUtil.Builder(activity, R.layout.base_default_dialog)
                        .setClose(R.id.tv_qx)
                        .Build()
                        .setText(R.id.tv_msg, "如果不打开通知的权限，则无法正常使用通知，是否跳转页面手动打开？")
                        .setOnClickListener(R.id.tv_qx, (v, builder) -> mNotificationUtil.goToSetNotify(activity));
                mDialogUtil.show();
            }
        }
    }

    /**
     * 检测sd卡的读取权限
     */
    public void checkSdPermissions(FragmentActivity activity) {
        if (activity != null) {
            // 请求app的读写权限
            new RxPermissionsUtil.Builder(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setSinglePerMissionListener((status, permission) -> LogUtil.e("权限：" + permission + " 状态：" + status))
                    .build()
                    .startRequestPermission();

        }
    }

    /**
     * 检测电池优化的权限，这个权限只有在android6.0之后才会去执行，低版本的手机也不用去考虑了，版本过低的话，也不会杀进程那么快的
     */
    public void checkBatteryPermissions(FragmentActivity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 判断电池时候被优化了
                if (mSystemUtil == null) {
                    mSystemUtil = SystemUtil.getInstance(activity.getApplication());
                }
                boolean ignoringBatteryOptimizations = mSystemUtil.isIgnoringBatteryOptimizations();
                if (!ignoringBatteryOptimizations) {
                    mDialogUtil = new DialogUtil.Builder(activity, R.layout.base_default_dialog)
                            .setClose(R.id.tv_qx)
                            .Build()
                            .setText(R.id.tv_msg, "请禁止电池优化功能，否则为了保持电量的消耗，会主动杀死App,无法进行系统的保活，是否禁止电池的优化？")
                            .setOnClickListener(R.id.tv_qd, (v, builder) -> {
                                // 申请打开电池优化
                                mSystemUtil.requestIgnoreBatteryOptimizations(activity);
                            })
                    ;
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
    public void checkAutoStartupPermissions(FragmentActivity activity) {
        if (activity != null) {
            mDialogUtil = new DialogUtil.Builder(activity, R.layout.base_default_dialog)
                    .setClose(R.id.tv_qx)
                    .Build()
                    .setText(R.id.tv_msg, "为了减少后台运行的时候，系统主动杀死App，请手动打开自动启动的权限，是否打开自动启动的权限？")
                    .setOnClickListener(R.id.tv_qd, (v, builder) -> {
                        // 申请打开电池优化
                        ActivityUtil.toSecureManager(activity);
                    });
            mDialogUtil.show();
        }
    }

    public String getServiceName() {
        if (TextUtils.isEmpty(mServiceName)) {
            mServiceName = AppLifecycleService.class.getName();
        }

        if (TextUtils.isEmpty(mServiceName)) {
            mServiceName = SpUtil.getString(CommonConstants.FILE_LIFECYCLE_SERVICE_NAME);
        }

        return mServiceName;
    }

    public String getJobServiceName() {
        if (TextUtils.isEmpty(mJobServiceName)) {
            mJobServiceName = AppJobService.class.getName();
        }

        if (TextUtils.isEmpty(mJobServiceName)) {
            mJobServiceName = SpUtil.getString(CommonConstants.FILE_LIFECYCLE_JOB_SERVICE_NAME);
        }
        return mJobServiceName;
    }
}
