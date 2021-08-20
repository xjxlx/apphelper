package android.helper.test.app;

import static com.android.helper.utils.NotificationUtil.CODE_REQUEST_ACTIVITY;

import android.Manifest;
import android.content.Intent;
import android.helper.R;
import android.helper.app.App;
import android.helper.test.app.account.AccountHelper;
import android.helper.test.app.keep.KeepManager;
import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.ActivityUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.LogWriteUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.RecycleUtil;
import com.android.helper.utils.RxPermissionsUtil;
import com.android.helper.utils.ServiceUtil;
import com.android.helper.utils.SystemUtil;
import com.android.helper.utils.ToastUtil;
import com.android.helper.utils.dialog.DialogUtil;

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
    public static final String FILE_NAME = "AppLifecycle";
    private DialogUtil mDialogUtil;
    private final int CODE_REQUEST_DC = 1000;
    private NotificationUtil mNotificationUtil;
    private android.widget.Button mBtStart;
    private androidx.recyclerview.widget.RecyclerView mTvContent;
    private AppLifecycleAdapter mAppLifecycleAdapter;
    private LogWriteUtil mWriteUtil;

    @Override
    protected int getBaseLayout() {
        return R.layout.activity_app_lifecycle;
    }

    @Override
    protected void initView() {
        super.initView();

        mBtStart = findViewById(R.id.bt_start);
        mTvContent = findViewById(R.id.tv_content);

        mAppLifecycleAdapter = new AppLifecycleAdapter(mContext);
        RecycleUtil.getInstance(mContext, mTvContent)
                .setVertical()
                .setAdapter(mAppLifecycleAdapter);

        mWriteUtil = new LogWriteUtil();

        KeepManager.getInstance().registerKeep(mContext);

        AccountHelper.addAccount(this);//添加账户
        AccountHelper.autoSync(mContext);//调用告知系统自动同步
    }

    @Override
    protected void initData() {
        super.initData();
        mNotificationUtil = NotificationUtil.getInstance(mContext);

        // 1：请求电池优化功能

        // 2:请求后台允许运行的功能
        initNotification();

        initNotificationDialog();

        mBtStart.setOnClickListener(v -> {
            if (mWriteUtil != null) {
                List<String> read = mWriteUtil.read(FILE_NAME);
                Collections.reverse(read);
                mAppLifecycleAdapter.setList(read);
            }
        });

        List<String> read = mWriteUtil.read(FILE_NAME);
        Collections.reverse(read);
        mAppLifecycleAdapter.setList(read);
    }

    private void initNotificationDialog() {
        // 检测notification的弹窗
        if (mNotificationUtil != null) {
            boolean openNotify = mNotificationUtil.checkOpenNotify(mContext);
            if (!openNotify) {
                // notification的弹窗
                mDialogUtil = DialogUtil
                        .getInstance()
                        .setContentView(mContext, R.layout.base_default_dialog)
                        .setText(R.id.tv_msg, "如果不打开通知的权限，则无法正常使用通知，是否跳转页面手动打开？")
                        .setOnClickListener(R.id.tv_qx, "取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialogUtil.dismiss();
                            }
                        })
                        .setOnClickListener(R.id.tv_qd, "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mNotificationUtil.goToSetNotify(mContext);
                                mDialogUtil.dismiss();
                            }
                        });
            } else {
                // 2：弹窗已经打开了，申请电池权限
                initDcDialog();
            }
        }
    }

    /**
     * 电池优化
     */
    private void initDcDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断电池时候被优化了
            SystemUtil instance = SystemUtil.getInstance(App.getInstance());
            boolean ignoringBatteryOptimizations = instance.isIgnoringBatteryOptimizations();
            if (!ignoringBatteryOptimizations) {
                mDialogUtil = DialogUtil
                        .getInstance()
                        .setContentView(mContext, R.layout.base_default_dialog)
                        .setText(R.id.tv_msg, "请禁止电池优化功能，否则为了保持电量的消耗，会主动杀死App,无法进行系统的保活，是否禁止电池的优化？")
                        .setOnClickListener(R.id.tv_qx, "取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialogUtil.dismiss();
                            }
                        })
                        .setOnClickListener(R.id.tv_qd, "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 申请打开电池优化
                                instance.requestIgnoreBatteryOptimizations(mContext, CODE_REQUEST_DC);
                                mDialogUtil.dismiss();
                            }
                        });
                mDialogUtil.show();
            } else {
                goXiaomiSetting();
            }
        } else {
            goXiaomiSetting();
        }
    }

    private void goXiaomiSetting() {
        mDialogUtil = DialogUtil
                .getInstance()
                .setContentView(mContext, R.layout.base_default_dialog)
                .setText(R.id.tv_msg, "为了减少后台运行的时候，系统主动杀死App，请手动打开后台运行权限，是否打开后台运行的权限？")
                .setOnClickListener(R.id.tv_qx, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogUtil.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_qd, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 申请打开电池优化
                        mDialogUtil.dismiss();
                        ActivityUtil.toSecureManager(mContext);
                    }
                });
        mDialogUtil.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("--->onNewIntent");
        initNotification();

        initNotificationDialog();
    }

    private void initNotification() {
        // 请求app的读写权限
        RxPermissionsUtil util = new RxPermissionsUtil(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        util.setAllPermissionListener((havePermission, permission) -> {
            if (havePermission) {
                // 3: 建立服务，提升服务的优先级到前台服务
                Intent intent = new Intent(mContext, AppLifecycleService.class);
                ServiceUtil.startService(mContext, intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_REQUEST_ACTIVITY:
                LogUtil.e("收到了notification的返回信息:" + resultCode);
                if (mNotificationUtil != null) {
                    boolean openNotify = mNotificationUtil.checkOpenNotify(mContext);
                    if (!openNotify) {
                        ToastUtil.show("消息通知权限打开异常！");
                    } else {
                        // 去设置电池的优化信息
                        initDcDialog();
                    }
                }
                break;
            case CODE_REQUEST_DC:
                LogUtil.e("电池优化的返回：" + resultCode);
                SystemUtil instance = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    instance = SystemUtil.getInstance(App.getInstance());
                    boolean ignoringBatteryOptimizations = instance.isIgnoringBatteryOptimizations();
                    if (ignoringBatteryOptimizations) {
                        goXiaomiSetting();
                    } else {
                        ToastUtil.show("电池优化打开异常！");
                    }
                } else {
                    goXiaomiSetting();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeepManager.getInstance().unregisterKeep(mContext);
    }
}