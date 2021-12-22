package com.android.helper.enums;

/**
 * @author : 流星
 * @CreateDate: 2021/12/22-4:04 下午
 * @Description:
 */
public enum DataEnum {
    /**
     * 显示固定的 天、小时、分钟、秒，如果没有就使用00：补全
     */
    DAY_HOURS_MINUTES_SECONDS,
    /**
     * 固定显示 小时、分钟、秒，数据不够就使用00：补全
     */
    HOURS_MINUTES_SECONDS,
    /**
     * 固定显示 分钟、秒，数据不够就使用00：补全
     */
    MINUTES_SECONDS,
    /**
     * 动态显示 天、小时、分钟、秒、 如果没有，前面的数据，就使用00：去补全，最低的限制要显示分钟和秒
     */
    AUTO_DIGITS

}
