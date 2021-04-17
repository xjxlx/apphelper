package android.helper.test;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.helper.R;
import android.helper.adapters.AppInfoAdapter;
import android.helper.bean.AppInfoBean;
import android.helper.databinding.ActivityControlAppBinding;
import android.helper.services.LookDogService;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.RxPermissionsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ControlAppTitleActivity extends BaseTitleActivity {

    private ActivityControlAppBinding binding;
    private List<AppInfoBean> mListAppInfo1 = new ArrayList<>();
    private List<AppInfoBean> mListAppInfo2 = new ArrayList<>();
    private AppInfoAdapter adapter;
    private long startTime;
    private long endTime;
    private UsageStatsManager usageStatsManager;

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityControlAppBinding.inflate(getLayoutInflater());

    }

    @Override
    protected void initData() {
        super.initData();

        runOnUiThread(() -> {
            // 获取应用的所有信息
            PackageManager pm = getPackageManager();
            // Return a List of all packages that are installed on the device.
            List<PackageInfo> packages = pm.getInstalledPackages(0);  // 不解析其他额外额信息
            for (PackageInfo packageInfo : packages) {
                AppInfoBean bean = new AppInfoBean();
                // app包名
                bean.setPackageName(packageInfo.packageName);
                // app名字
                bean.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                // app的图标
                bean.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
                // 文件大小
                bean.setAppSize(packageInfo.applicationInfo.sourceDir.length());

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.setSystem(true);
                    mListAppInfo1.add(bean);
                } else {
                    bean.setSystem(false);
                    mListAppInfo2.add(bean);
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new AppInfoAdapter(mContext, mListAppInfo1, mListAppInfo2);
        binding.rvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvList.setAdapter(adapter);

        RxPermissionsUtil util = new RxPermissionsUtil(mContext,
                Manifest.permission.PACKAGE_USAGE_STATS,
                Manifest.permission.FOREGROUND_SERVICE
        );
        util.setAllPermissionListener((havePermission, permission) -> LogUtil.e("是否拥有权限：" + havePermission));

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_WEEK, -2);
        startTime = calendar.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                //若未授权则请求权限
                Intent intent3 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent3.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent3, 123);
            }
        } else {
            // 跳转应用
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStats == null || usageStats.size() == 0) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        Intent intent = new Intent(mContext, LookDogService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            // 跳转应用
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStats == null || usageStats.size() == 0) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_control_app;
    }
}