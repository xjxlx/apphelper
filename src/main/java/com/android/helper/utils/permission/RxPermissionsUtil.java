package com.android.helper.utils.permission;

import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.AppUtil;
import com.android.helper.utils.LogUtil;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 简单封装的权限工具类
 * <ol>
 *     权限的描述：
 *         Dangerous Permission：还是得写在xml文件里，但是App安装时具体如果执行授权分以下几种情况：
 *      1、targetSDKVersion < 23 & API(手机系统) < 6.0 ：安装时默认获得权限，且用户无法在安装App之后取消权限。
 *      2、targetSDKVersion >= 23 & API(手机系统) < 6.0 ：安装时默认获得权限，且用户无法在安装App之后取消权限。
 *      3、targetSDKVersion < 23 & API(手机系统) >= 6.0 ：安装时默认获得权限，但是用户可以在安装App完成后动态取消授权（ 取消时手机会弹出提醒，告诉用户这个是为旧版手机打造的应用，让用户谨慎操作 ）。
 *      4、targetSDKVersion >= 23 & API(手机系统) >= 6.0 ：安装时不会获得权限，可以在运行时向用户申请权限。用户授权以后仍然可以在设置界面中取消授权，用户主动在设置界面取消后，在app运行过程中可能会出现crash。
 * </ol>
 */
public class RxPermissionsUtil implements BaseLifecycleObserver {

    private String[] mPermissions;
    private FilterPerMission[] mFilterMission;
    private PermissionsCallBackListener mAllPermissionsListener;
    private SinglePermissionsCallBackListener mSinglePermissionsListener;
    private RxPermissions mRxPermissions;
    private Disposable mSubscribe;

    /**
     * 开发的目标版本
     */
    private final int TARGET_VERSION = AppUtil.getInstance().getTargetSdkVersion();

    /**
     * 手机系统的当前版本
     */
    private final int SDK_INT = Build.VERSION.SDK_INT;

    public RxPermissionsUtil(Builder builder) {
        FragmentActivity mFragmentActivity = null;
        Fragment mFragment = null;
        if (builder != null) {
            mFragmentActivity = builder.mFragmentActivity;
            mFragment = builder.mFragment;
            this.mPermissions = builder.mPermissions;
            this.mFilterMission = builder.mFilterMission;
            this.mAllPermissionsListener = builder.mAllPermissionsListener;
            this.mSinglePermissionsListener = builder.mSinglePermissionsListener;

            //1:activity  2:Fragment
            int type = builder.type;

            if (type == 1) {
                if (mFragmentActivity != null) {
                    mRxPermissions = new RxPermissions(mFragmentActivity);
                    Lifecycle lifecycle = mFragmentActivity.getLifecycle();
                    lifecycle.addObserver(this);
                }
            } else if (type == 2) {
                if (mFragment != null) {
                    mRxPermissions = new RxPermissions(mFragment);
                    Lifecycle lifecycle = mFragment.getLifecycle();
                    lifecycle.addObserver(this);
                }
            }
        }
    }

    /**
     * @return 检测默认的版本是否需要权限，如果用户的手机版本大于6.0，且目标版本也大于6.0,就都需要去检测，否则就默认拥有权限
     * <p>
     * 1、targetSDKVersion < 23 & API(手机系统) < 6.0 ：安装时默认获得权限，且用户无法在安装App之后取消权限。
     * 2、targetSDKVersion >= 23 & API(手机系统) < 6.0 ：安装时默认获得权限，且用户无法在安装App之后取消权限。
     * 3、targetSDKVersion < 23 & API(手机系统) >= 6.0 ：安装时默认获得权限，但是用户可以在安装App完成后动态取消授权（ 取消时手机会弹出提醒，告诉用户这个是为旧版手机打造的应用，让用户谨慎操作 ）。
     * 4、targetSDKVersion >= 23 & API(手机系统) >= 6.0 ：安装时不会获得权限，可以在运行时向用户申请权限。用户授权以后仍然可以在设置界面中取消授权，用户主动在设置界面取消后，在app运行过程中可能会出现crash。
     */
    private boolean checkMustVersion() {
        boolean mustVersion = false;

        // Android 6.0的分割线
        int m = Build.VERSION_CODES.M;

        /*
         * 1:只有当手机的系统大于23，也就是6.0的时候，才用得上去申请权限
         * 2:只有开发环境的目标版本大于23，才用得上去申请动态权限，否则都不用申请
         */
        if ((SDK_INT >= m) && (TARGET_VERSION >= m)) {
            mustVersion = true;
        }
        return mustVersion;
    }

    /**
     * @return 过滤掉不适合的权限
     */
    public String[] filterPermission() {
        if (mPermissions != null && mPermissions.length > 0 && mFilterMission != null && mFilterMission.length > 0) {
            LogUtil.e("原始的权限为：" + Arrays.toString(mPermissions));
            // 把数组权限，转换为集合权限，用于删除数据
            ArrayList<String> tempPermission = new ArrayList<>();
            Collections.addAll(tempPermission, mPermissions);

            // 过滤数据
            for (FilterPerMission filter : mFilterMission) {
                String filterPermission = filter.getPermission();
                int targetVersion = filter.getTargetVersion();

                // 如果项目的目标版本 小于 限定的版本，则无需去请求这个权限
                if (TARGET_VERSION < targetVersion) {
                    // 删除掉指定的权限
                    tempPermission.remove(filterPermission);
                }
            }

            // 如果数据发生了改变，就去重新赋值对象
            if (tempPermission.size() != mPermissions.length) {
                // 把数组重新转换集合
                mPermissions = new String[tempPermission.size()];
                for (int i = 0; i < tempPermission.size(); i++) {
                    String s = tempPermission.get(i);
                    mPermissions[i] = s;
                }
            }
        }
        return mPermissions;
    }

    /**
     * @return 请求权限结果，并返回接口的回调
     */
    public RxPermissionsUtil startRequestPermission() {
        // 过滤权限
        String[] filterPermission = filterPermission();
        if (mRxPermissions != null && filterPermission != null && filterPermission.length > 0) {
            LogUtil.e("过滤后的权限为：" + Arrays.toString(filterPermission));

            // 版本满足的时候才会去检测权限
            boolean mustVersion = checkMustVersion();
            if (mustVersion) {

                if (mAllPermissionsListener != null) { // 请求全部权限
                    mSubscribe = mRxPermissions
                            .request(filterPermission)
                            .subscribe(granted -> {
                                if (granted) { // Always true pre-M
                                    // I can control the camera now
                                } else {
                                    // Oups permission denied
                                }
                                // 返回拥有全部权限的结果
                                mAllPermissionsListener.onRxPermissions(granted);
                            });

                } else if (mSinglePermissionsListener != null) { // 请求单个权限

                    mSubscribe = mRxPermissions
                            .requestEach(filterPermission)
                            .subscribe(permission -> { // will emit 2 Permission objects
                                if (permission.granted) {
                                    // `permission.name` is granted !
                                    // 授权允许
                                    mSinglePermissionsListener.onRxPermissions(1, permission.name);
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    // Denied permission without ask never again
                                    // 授权拒绝
                                    mSinglePermissionsListener.onRxPermissions(2, permission.name);
                                } else {
                                    // Denied permission with ask never again
                                    // Need to go to the settings
                                    // 授权拒绝，并不在提示
                                    mSinglePermissionsListener.onRxPermissions(3, permission.name);
                                }
                            });
                }
            } else {
                // 版本不满足，则默认拥有权限
                if (mAllPermissionsListener != null) {
                    mAllPermissionsListener.onRxPermissions(true);
                } else if (mSinglePermissionsListener != null) {
                    mSinglePermissionsListener.onRxPermissions(1, "");
                }
            }
        }
        return this;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

        if (mPermissions != null) {
            mPermissions = null;
        }

        if (mAllPermissionsListener != null) {
            mAllPermissionsListener = null;
        }

        if (mRxPermissions != null) {
            mRxPermissions = null;
        }

        if (mSinglePermissionsListener != null) {
            mSinglePermissionsListener = null;
        }

        if (mSubscribe != null) {
            if (!mSubscribe.isDisposed()) {
                mSubscribe.dispose();
            }
            mSubscribe = null;
        }
    }

    public static class Builder {
        private FragmentActivity mFragmentActivity;
        private Fragment mFragment;
        private final String[] mPermissions;
        private FilterPerMission[] mFilterMission;
        private PermissionsCallBackListener mAllPermissionsListener;
        private SinglePermissionsCallBackListener mSinglePermissionsListener;
        private int type; //1:activity  2:Fragment

        /**
         * @param permissions 举例：Manifest.permission.ACCESS_FINE_LOCATION
         */
        public Builder(FragmentActivity fragmentActivity, String... permissions) {
            mFragmentActivity = fragmentActivity;
            mPermissions = permissions;
            type = 1;
        }

        /**
         * @param permissions 举例：Manifest.permission.ACCESS_FINE_LOCATION
         */
        public Builder(Fragment fragment, String... permissions) {
            mFragment = fragment;
            mPermissions = permissions;
            type = 2;
        }

        /**
         * @return 所有的权限一起回调，只要最后的结果
         */
        public Builder setAllPerMissionListener(PermissionsCallBackListener allPerMissionListener) {
            this.mAllPermissionsListener = allPerMissionListener;
            mSinglePermissionsListener = null;
            return this;
        }

        /**
         * @return 单个权限的回调
         */
        public Builder setSinglePerMissionListener(SinglePermissionsCallBackListener singlePerMissionListener) {
            this.mSinglePermissionsListener = singlePerMissionListener;
            mAllPermissionsListener = null;
            return this;
        }

        // <editor-fold desc="过滤权限">

        /**
         * <ol>
         *     某些权限，只有在固定的版本上，才会有，在其他的版本上不会出现，因此需要判断版本，过滤掉不符合版本的权限，避免返回异常
         *     例如：后台定位权限：FOREGROUND_SERVICE,这个权限是在28上面才有的，低于这个版本就不应该去判断，否则就会导致权限的
         *          回调异常，所有需要对版本进行匹配，过滤掉不适用的权限
         * </ol>
         *
         * @return 过滤权限
         */
        public Builder setFilterPermission(FilterPerMission... filterPerMissions) {
            mFilterMission = filterPerMissions;
            return this;
        }
        //</editor-fold>

        public RxPermissionsUtil build() {
            return new RxPermissionsUtil(this);
        }

    }

}
