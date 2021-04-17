package com.android.helper.utils.sqlite;

/**
 * SQLite的实体，所有的Sql相关的都要用到
 */
public class SQLEntity {
    private String tableName; // 表名字，例如：user.db,这里只传user
    private String sql; // 具体的sql语句，可以使用SQLiteTableManager 中的 CreateSQL 方法去创建
    private int versionCode; // 版本号，在升级的时候会用到

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "SQLEntity{" +
                "tableName='" + tableName + '\'' +
                ", sql='" + sql + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
