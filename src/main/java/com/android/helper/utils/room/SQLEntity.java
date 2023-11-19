package com.android.helper.utils.room;

/**
 * Sql 添加参数的实体
 */
public class SQLEntity {

    public final String notNULL = "NOT NULL";
    private String unit;        // 添加字段的单位
    private String defaultValue; // 默认的值

    public SQLEntity(String unit) {
        this.unit = unit;
    }

    public SQLEntity(String unit, String defaultValue) {
        this.unit = unit;
        this.defaultValue = defaultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
