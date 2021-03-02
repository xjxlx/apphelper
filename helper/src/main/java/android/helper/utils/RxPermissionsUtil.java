package android.helper.utils;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions.RxPermissions;
import android.helper.interfaces.listener.AllPermissionsListener;
import android.helper.interfaces.listener.SinglePermissionsListener;

/**
 * 简单封装的权限工具类
 */
public class RxPermissionsUtil {
    
    private FragmentActivity activity;
    private String[] permissions;
    
    /**
     * @param activity    FragmentActivity 的对象
     * @param permissions 具体检测的权限    举例：Manifest.permission.ACCESS_FINE_LOCATION
     */
    public RxPermissionsUtil(FragmentActivity activity, final String... permissions) {
        this.activity = activity;
        this.permissions = permissions;
    }
    
    /**
     * @return 权限的单个回调，并带有权限的返回
     */
    public RxPermissionsUtil setAllPermissionListener(AllPermissionsListener permissionListener) {
        new RxPermissions(activity)
                .requestEach(permissions)
                .subscribe(permission -> {
                    if (permissionListener != null) {
                        permissionListener.onRxPermissions(permission.granted, permission);
                    }
                });
        return this;
    }
    
    /**
     * @param listener 权限的接口回调
     * @return 返回合并权限的结果，第二个参数返回为null
     */
    public RxPermissionsUtil setSinglePermissionListener(SinglePermissionsListener listener) {
        new RxPermissions(activity).request(permissions).subscribe(aBoolean -> {
            if (listener != null) {
                listener.onRxPermissions(aBoolean);
            }
        });
        return this;
    }
    
}
