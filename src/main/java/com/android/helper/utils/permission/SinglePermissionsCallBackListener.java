package com.android.helper.utils.permission;

public interface SinglePermissionsCallBackListener {

    /**
     * @param status     1:授权允许，2：授权拒绝，3：授权拒绝，并不在提示，这个时候需要进入到设置页面去设置
     * @param permission 权限的名字
     */
    void onRxPermissions(int status, String permission);
}
