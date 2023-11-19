package com.android.helper.utils.sqlite;

import android.text.TextUtils;

import com.android.common.utils.LogUtil;

/**
 * Sqlite数据库表的工具类,
 */
public class SQLiteTableManager {

    // sqlite 当前数据库的表名
    public static final String KEY_SQLITE_CURRENT_TABLE_NAME = "key_sqlite_current_table_name";
    // sqlite 当前数据库的sql
    public static final String KEY_SQLITE_CURRENT_SQL = "key_sqlite_current_sql";
    // sqlite 当前数据库需要更新的sql
    public static final String KEY_SQLITE_CURRENT_UPDATE_SQL = "key_sqlite_current_update_sql";

    // 环信的数据库,用户的信息
    public static final String TABLE_IM_USER_INFO = "table_im_user_info";

    /**
     * 创建数据库表
     *
     * @param tableName 指定表
     * @param sql       表中的字段
     */
    public static String CreateSQL(String tableName, String[] sql) {
        String sqlResult = "";
        if ((!TextUtils.isEmpty(tableName)) && (sql != null) && (sql.length > 0)) {
            StringBuilder SqlContent = new StringBuilder();
            SqlContent = new StringBuilder("create table if not exists " + tableName + " (" + "id integer primary key autoincrement, ");
            for (int i = 0; i < sql.length; i++) {
                String key = sql[i];
                if (i != sql.length - 1) {
                    SqlContent.append(key).append(" text, ");
                } else {
                    SqlContent.append(key).append(" text)");
                }
            }
            // 把创建的sql语句存入到本地
            sqlResult = SqlContent.toString();
            LogUtil.e("创建数据库的SQL语句为：" + SqlContent);
        }
        return sqlResult;
    }

    /**
     * 指定数据库中的表，增加一个指定的列
     *
     * @param tableName 指定的表
     * @param sql       指定的字段，每次只能增加一个
     */
    public static String AddSQL(String tableName, String sql) {
        String sqlResult = "";
        if ((!TextUtils.isEmpty(tableName)) && (!TextUtils.isEmpty(sql))) {
            String content = "alter table " + tableName + " add " + sql + " text";
            sqlResult = content;
            LogUtil.e("更新数据库的SQL语句为：" + content);
        }
        return sqlResult;
    }
}
