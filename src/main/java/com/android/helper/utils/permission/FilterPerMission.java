package com.android.helper.utils.permission;

/**
 * @author : 流星
 * @CreateDate: 2021/12/5-15:27
 * @Description: 过滤权限的版本
 */
public class FilterPerMission {

    /**
     * Manifest.permission.ACCESS_COARSE_LOCATION
     */
    private String permission; // 权限名字
    private int targetVersion; // 对应的目标版本

    public FilterPerMission(String permission, int targetVersion) {
        this.permission = permission;
        this.targetVersion = targetVersion;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(int targetVersion) {
        this.targetVersion = targetVersion;
    }

    @Override
    public String toString() {
        return "FilterPerMission{" +
                "permission='" + permission + '\'' +
                ", targetVersion=" + targetVersion +
                '}';
    }
}
