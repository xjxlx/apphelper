package android.helper.bean;

import android.graphics.drawable.Drawable;

import com.android.helper.base.BaseEntity;

public class AppInfoBean extends BaseEntity {

    private String appName;
    private Drawable appIcon;
    private String packageName;
    private long appSize;
    private boolean isSystem;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public AppInfoBean() {
    }

}
