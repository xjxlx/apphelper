package com.android.helper.utils.permission;

/**
 * 权限工具的接口回调，所有权限全部回调
 */
public interface PermissionsCallBackListener {
    void onRxPermissions(boolean haveAllPermission);
}
