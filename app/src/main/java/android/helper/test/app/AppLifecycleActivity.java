package android.helper.test.app;

import static com.android.helper.utils.SystemUtil.CODE_REQUEST_ACTIVITY_BATTERY;

import android.content.Intent;
import android.helper.R;
import android.helper.app.App;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.LogWriteUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.RecycleUtil;
import com.android.helper.utils.SystemUtil;
import com.android.helper.utils.ToastUtil;

import java.util.Collections;
import java.util.List;

/**
 * App保活的一个实现方案
 * 实现逻辑：
 * 1：建立一个service，并把服务提升到前台服务，在服务中去不停的刷新notification去保持前台的一个活性
 * 2：打开后台的电池优化
 * 3：打开后台的允许运行功能
 * <p>
 * 4:在8.0以下，使用一个像素的activity去保活
 * 5:JobService 轮询拉活
 * 6：账号拉活
 */
public class AppLifecycleActivity extends BaseActivity {
    public static final String FILE_NAME = "App保活：";
    private android.widget.Button mBtStart;
    private AppLifecycleAdapter mAppLifecycleAdapter;
    private LogWriteUtil mWriteUtil;
    private LifecycleManager mLifecycleManager;
    private NotificationUtil mNotificationUtil;
    private SystemUtil mSystemUtil;

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_app_lifecycle;
    }

    @Override
    protected void initView() {
        super.initView();

        mBtStart = findViewById(R.id.bt_start);
        RecyclerView tvContent = findViewById(R.id.tv_content);

        mAppLifecycleAdapter = new AppLifecycleAdapter(mContext);
        RecycleUtil.getInstance(mContext, tvContent)
                .setVertical()
                .setAdapter(mAppLifecycleAdapter);

        mWriteUtil = new LogWriteUtil();
    }

    @Override
    protected void initData() {
        super.initData();

        // 开启保活的流程
        startLifecycle();

        mBtStart.setOnClickListener(v -> {
            if (mWriteUtil != null) {
                List<String> read = mWriteUtil.read(FILE_NAME);
                if (read != null && read.size() > 0) {
                    Collections.reverse(read);
                }
                mAppLifecycleAdapter.setList(read);
            }
        });

        List<String> read = mWriteUtil.read(FILE_NAME);
        if (read != null && read.size() > 0) {
            Collections.reverse(read);
        }
        mAppLifecycleAdapter.setList(read);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("--->onNewIntent");
        startLifecycle();
    }

    private void startLifecycle() {
        if (mLifecycleManager == null) {
            mLifecycleManager = LifecycleManager.getInstance();
        }

        if (mNotificationUtil == null) {
            mNotificationUtil = NotificationUtil.getInstance(mContext);
        }

        if (mSystemUtil == null) {
            mSystemUtil = SystemUtil.getInstance(App.getInstance());
        }

        // 检测notification的权限
        boolean openNotify = mNotificationUtil.checkOpenNotify(mContext);
        //  检测充电的权限
        boolean ignoringBatteryOptimizations = mSystemUtil.isIgnoringBatteryOptimizations();

        if (!openNotify) {
            // 1：检测notification
            mLifecycleManager.checkNotificationPermissions(mContext);
        } else {
            if (ignoringBatteryOptimizations) {
                // 2: 检测充电的权限
                mLifecycleManager.checkAutoStartupPermissions(mContext);
            }
        }

        // 2：打开保活的流程
        mLifecycleManager.startLifecycle(mContext.getApplication());

        mLifecycleManager.setLifecycleListener(tag -> LogUtil.e("我已经开始回调了服务中的数据！"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NotificationUtil.CODE_REQUEST_ACTIVITY_NOTIFICATION:
                LogUtil.e("收到了notification的返回信息:" + resultCode);
                if (mNotificationUtil != null) {
                    boolean openNotify = mNotificationUtil.checkOpenNotify(mContext);
                    if (!openNotify) {
                        ToastUtil.show("消息通知权限打开异常！");
                    } else {
                        // 检测电池优化的权限
                        if (mLifecycleManager != null) {
                            mLifecycleManager.checkAutoStartupPermissions(mContext);
                        }
                    }
                }
                break;
            case CODE_REQUEST_ACTIVITY_BATTERY:
                LogUtil.e("电池优化的返回：" + resultCode);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (mSystemUtil != null) {
                        boolean ignoringBatteryOptimizations = mSystemUtil.isIgnoringBatteryOptimizations();
                        if (ignoringBatteryOptimizations) {
                            // 打开自动启动的权限
                            if (mLifecycleManager != null) {
                                mLifecycleManager.checkAutoStartupPermissions(mContext);
                            }
                        } else {
                            ToastUtil.show("电池优化打开异常！");
                        }
                    }
                } else {
                    if (mLifecycleManager != null) {
                        mLifecycleManager.checkAutoStartupPermissions(mContext);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (mLifecycleManager != null) {
//            mLifecycleManager.stopLifecycle(mContext);
//        }
    }
}