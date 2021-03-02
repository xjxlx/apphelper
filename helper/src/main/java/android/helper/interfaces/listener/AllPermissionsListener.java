package android.helper.interfaces.listener;

import com.tbruyelle.rxpermissions.Permission;

/**
 * 权限工具的接口回调，所有权限全部回调
 */
public interface AllPermissionsListener {
    void onRxPermissions(boolean havePermission, Permission permission);
}
