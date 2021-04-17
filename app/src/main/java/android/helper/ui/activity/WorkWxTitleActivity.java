package android.helper.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.helper.R;
import android.helper.databinding.ActivityWorkWxBinding;
import android.helper.services.QywxService;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.helper.base.BaseTitleActivity;

/**
 * 企业微信的界面
 */
public class WorkWxTitleActivity extends BaseTitleActivity {

    private ActivityWorkWxBinding binding;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_work_wx;
    }

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityWorkWxBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initData() {
        super.initData();

        Intent intent = new Intent(this, QywxService.class);

        binding.btnStart.setOnClickListener(v -> {
            boolean enabled = isEnabled();
            if (!enabled) {
                Intent intents = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intents);
                return;
            }

            startService(intent);
            Log.e(QywxService.TAG, "开启了监听！ ");
        });

        binding.btnStop.setOnClickListener(v -> {
            stopService(intent);
            Log.e(QywxService.TAG, "关闭了监听！ ");
        });

        binding.btnSwitch.setOnClickListener(v -> {
            Log.e(QywxService.TAG, "打开开关！ ");
            Intent intents = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intents);
        });

    }

    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        // 先关闭，在打开
        pm.setComponentEnabledSetting(new ComponentName(context, QywxService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, QywxService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 判断 Notification access 是否开启
     *
     * @return
     */
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}