package com.android.helper.common;

/**
 * 公共常量的存储类
 */
public class CommonConstants {

    /* *************************************** key start **********************************************/
    /**
     * 状态栏高度
     */
    public static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    /**
     * webView的url
     */
    public static final String KEY_BASE_WEB_VIEW_URL = "key_base_web_view_url";

    public static final String KEY_LIFECYCLE_FROM = "key_lifecycle_from";
    public static final String KEY_LIFECYCLE_NOTIFICATION_CHANNEL_NAME = "key_lifecycle_notification_channel_name";

    /**
     * 蓝牙扫描
     */
    public static final String FILE_BLUETOOTH_NAME = "bluetooth";

    /**
     * 保活日志的文件名字
     */
    public static final String FILE_LIFECYCLE_NAME = "保活";

    /**
     * 充电中心的文件名字
     */
    public static final String FILE_CHARGING_CENTER_NAME = "充电中心";

    /**
     * 保活 服务的名字
     */
    public static final String FILE_LIFECYCLE_SERVICE_NAME = "file_lifecycle_service_name";

    /**
     * 保活 JOB服务的名字
     */
    public static final String FILE_LIFECYCLE_JOB_SERVICE_NAME = "file_lifecycle_job_service_name";

    /* *************************************** key end **********************************************/

    /* *************************************** code start **********************************************/
    /**
     * dialog打开的code
     */
    public static final int CODE_DIALOG_SHOW = 1000;
    /**
     * dialog关闭的code
     */
    public static final int CODE_DIALOG_DISMISS = CODE_DIALOG_SHOW + 1;

    /**
     * Banner的轮播长度
     */
    public static final int BANNER_LENGTH = 100000;
    /* *************************************** code end **********************************************/

}
