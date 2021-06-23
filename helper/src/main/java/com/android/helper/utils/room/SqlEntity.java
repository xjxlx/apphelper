package com.android.helper.utils.room;

/**
 * Sql 添加参数的实体
 */
public class SqlEntity {

    private String unit;        // 添加字段的单位
    private boolean canNot;    // 被添加的字段是否允许为null
    private String defaultValue; // 默认的值
    public final String notNULL = "NOT NULL";

    public SqlEntity(String unit) {
        this.unit = unit;
    }

    /**
     * @param unit   添加字段的单位
     * @param canNot 被添加的字段是否允许为null
     */
    public SqlEntity(String unit, boolean canNot) {
        this.unit = unit;
        this.canNot = canNot;
    }

    public SqlEntity(String unit, boolean canNot, String defaultValue) {
        this.unit = unit;
        this.canNot = canNot;
        this.defaultValue = defaultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isCanNot() {
        return canNot;
    }

    public void setCanNot(boolean canNot) {
        this.canNot = canNot;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "SqlEntity{" +
                "unit='" + unit + '\'' +
                ", canNot=" + canNot +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
