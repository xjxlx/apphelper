package com.android.helper.utils.permission;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 简单封装的权限工具类
 */
public class RxPermissionsUtil implements BaseLifecycleObserver {

    private String[] mPermissions;
    private PermissionsCallBackListener mAllPermissionsListener;
    private SinglePermissionsCallBackListener mSinglePermissionsListener;
    private RxPermissions mRxPermissions;
    private Disposable mSubscribe;

    public RxPermissionsUtil(Builder builder) {
        FragmentActivity mFragmentActivity = null;
        Fragment mFragment = null;
        if (builder != null) {
            mFragmentActivity = builder.mFragmentActivity;
            mFragment = builder.mFragment;
            this.mPermissions = builder.mPermissions;
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
     * @return 请求权限结果，并返回接口的回调
     */
    public RxPermissionsUtil startRequestPermission() {
        if (mRxPermissions != null && mPermissions != null) {
            if (mAllPermissionsListener != null) { // 请求全部权限
                mSubscribe = mRxPermissions
                        .request(mPermissions)
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
                        .requestEach(mPermissions)
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

        public RxPermissionsUtil build() {
            return new RxPermissionsUtil(this);
        }

    }

}
