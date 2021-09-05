package com.android.helper.utils.account;

/**
 * 保活方案的枚举
 */
public enum LifecycleAppEnum {

    /**
     * 来源与JobService
     */
    FROM_JOB("JOB"),
    /**
     * 来源与账号拉活
     */
    FROM_ACCOUNT("ACCOUNT"),
    /**
     * 来源于intent的启动
     */
    From_Intent("INTENT"),

    /**
     * 来源于Service的启动
     */
    FROM_SERVICE("SERVICE");

    private final String from;

    LifecycleAppEnum(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

}
