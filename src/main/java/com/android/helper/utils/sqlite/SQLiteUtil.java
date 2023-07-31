package com.android.helper.utils.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.common.utils.LogUtil;
import com.android.helper.utils.ConvertUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的帮助类
 * <p>
 * 使用步骤：
 * 1:首先要使用{@link SQLiteTableManager#CreateSQL(String, String[])}方法去创建数据库的SQL语句，添加指定的字段
 * --->或者使用{@link SQLiteTableManager#AddSQL(String, String)} 方法去新增一个字段，
 * --->注意：Sqlite并不支持删除自定义的列，所以只能新增列，不能删除列，如果需要删除，就删掉App，重新创建
 * 2：{@link SQLiteTableManager}类是数据库表的工具类，用于封装新建的表，可以自定义实现，建议都放到一块
 * 3：{@link SqliteFieldManager}类是表中字段的封装类，用于封装表中的字段，可以自定义实现，建议都放到一块
 * <p>
 * --->增删改查
 * --->增加：{@link #insertNoRepeat(String[], String[], String, String)}根据指定的唯一值添加字段，如果重复就更新，否则就添加
 * --->删除：{@link #delete(String, String)}根据指定的key，删除一行数据
 * --->修改：{@link #update(List, List, String, String)}根据指定的条件，修改需要修改的数据
 * --->查询指定列：{@link #query(String[], String, String)}根据指定的条件，查询表中其他的数据
 * --->查询表中时候包含某个唯一的字段：{@link #queryContains(String, String)}
 * --->查询指定表中的所有数据：{@link #queryTableAll()}
 * --->查询表中某列的全部数据：{@link #queryList(String[])}
 * <p>
 * ------------------ 注意--------------
 */

public class SQLiteUtil {
    private final String TAG = "SQL";
    private String mTableName;// 数据库的表名
    private SQLiteManager sqliteHelper;
    private SQLiteDatabase mDataBase;
    private Cursor cursor;

    /**
     * @param sqlEntity sql的具体对象
     * @return 返回数据库的对象
     */
    public static SQLiteUtil getInstance(Context context, SQLEntity sqlEntity) {
        return new SQLiteUtil(context, sqlEntity);
    }

    private SQLiteUtil(Context context, SQLEntity sqlEntity) {
        sqliteHelper = SQLiteManager.getInstance(context, sqlEntity);
    }

    /**
     * @param attributeKey   需要插入的属性
     * @param attributeValue 需要插入属性的值
     * @param selectorKey    主键key
     * @param selectorValue  主键的value
     * @return 插入数据库相应的字段, 不会插入重复的字段
     */
    public boolean insertNoRepeat(String[] attributeKey, String[] attributeValue, String selectorKey, String selectorValue) {
        boolean isSuccess = false;
        if ((attributeKey != null) && (attributeValue != null) && (attributeKey.length == attributeValue.length)) {
            // 查询表中是否有这个字段
            boolean query = queryContains(selectorKey, selectorValue);
            if (query) {
                LogUtil.e(TAG, "重复数据，更新！");
                // 更新数据
                List<String> keys = ConvertUtil.filterList(ConvertUtil.ArrayToList(attributeKey), selectorKey);
                List<String> values = ConvertUtil.filterList(ConvertUtil.ArrayToList(attributeValue), selectorValue);

                update(keys, values, selectorKey, selectorValue);
                isSuccess = true;
            } else {
                LogUtil.e(TAG, "空白数据，添加！");
                // 3:执行sql方式插入字段
                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < attributeKey.length; i++) {
                    String key = attributeKey[i];
                    String value = attributeValue[i];
                    contentValues.put(key, value);
                }
                try {
                    // 3:执行sql方式插入字段
                    long insert = getSqlDataBase().insert(mTableName, null, contentValues);
                    if (insert != -1) {
                        isSuccess = true;
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "插入数据库错误：" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // 关闭数据库
                    closeDb();
                }
            }
        } else {
            LogUtil.e(TAG, "插入数据库中的字段或者值有问题，或者两者长度不相同");
        }
        return isSuccess;
    }

    /**
     * @param key
     * @return 根据unid查询单个的条目
     */

    /**
     * @param query         需要查询的列
     * @param selection     根据哪个字段去查询
     * @param selectonValue 需要查询的字段的具体值
     * @return 查询指定表中某些字段的对象
     */
    public List<JsonObject> query(String[] query, String selection, String selectonValue) {
        List<JsonObject> list = null;
        if ((query != null) && (query.length > 0) && (!TextUtils.isEmpty(selection)) && (!TextUtils.isEmpty(selectonValue))) {
            // 打开数据库
            // 查询数据
            /**
             * String table, :表名
             * String[] columns, ：查询哪些列 ,如果传null代表查询所有列
             * String selection, ：查询的条件
             * String[] selectionArgs, ：条件占位符的值
             * String groupBy, ：按什么分组
             * String having,String orderBy ：按什么排序
             */

            try {
                cursor = getSqlDataBase().query(mTableName, query, (selection + ("=?")), new String[]{selectonValue}, null, null, null);
                // 4:解析cursor对象 getCount:返回结果集中的行数，如果为空的话就不必去查询了
                if (cursor != null && cursor.getCount() > 0) {
                    list = new ArrayList<>();
                    // 下一行是否还有数据 moveToNext：如果往后面移动返回就为true，否则就是数据没有了
                    while (cursor.moveToNext()) {
                        JsonObject object = new JsonObject();

                        for (int i = 0; i < query.length; i++) {
                            String key = query[i];
                            // 根据列中对象的名称返回该列对象的索引 getColumnIndex（）：根据该列中对象的名称返回该列对象的索引
                            String value = cursor.getString(cursor.getColumnIndex(key));
                            object.addProperty(key, value);
                        }
                        list.add(object);
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "查询数据库集合错误：" + e.getMessage());
                e.printStackTrace();
            } finally {
                closeDb();
            }
            return list;
        }
        return null;
    }

    /**
     * @return 查询指定表中所有的数据
     */
    public List<JsonObject> queryTableAll() {
        List<JsonObject> list = null;
        // 打开数据库
        // 查询数据
        /**
         * String table, :表名
         * String[] columns, ：查询哪些列 ,如果传null代表查询所有列
         * String selection, ：查询的条件
         * String[] selectionArgs, ：条件占位符的值
         * String groupBy, ：按什么分组
         * String having,String orderBy ：按什么排序
         */

        try {
            cursor = getSqlDataBase().query(mTableName, null, null, null, null, null, null);
            // 4:解析cursor对象 getCount:返回结果集中的行数，如果为空的话就不必去查询了
            if (cursor != null && cursor.getCount() > 0) {
                list = new ArrayList<>();
                // 下一行是否还有数据 moveToNext：如果往后面移动返回就为true，否则就是数据没有了
                while (cursor.moveToNext()) {
                    JsonObject object = new JsonObject();
                    String[] columnNames = cursor.getColumnNames();
                    for (int i = 0; i < columnNames.length; i++) {
                        String columnName = columnNames[i];
                        String value = cursor.getString(cursor.getColumnIndex(columnName));
                        object.addProperty(columnName, value);
                    }

                    list.add(object);
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "查询数据库集合错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDb();
        }
        return list;
    }

    /**
     * @param key   查询的列
     * @param value 查询的值
     * @return 查询表中某列是否包含某个字段
     */
    public boolean queryContains(String key, String value) {
        if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(value))) {
            // 查询数据
            /**
             * String table, :表名
             * String[] columns, ：查询哪些列 ,如果传null代表查询所有列
             * String selection, ：查询的条件
             * String[] selectionArgs, ：条件占位符的值
             * String groupBy, ：按什么分组
             * String having,String orderBy ：按什么排序
             */

            try {
                cursor = getSqlDataBase().query(mTableName, new String[]{key}, null, null, null, null, null);
                // 4:解析cursor对象 getCount:返回结果集中的行数，如果为空的话就不必去查询了
                if (cursor != null && cursor.getCount() > 0) {
                    // 下一行是否还有数据 moveToNext：如果往后面移动返回就为true，否则就是数据没有了
                    while (cursor.moveToNext()) {
                        // 根据列中对象的名称返回该列对象的索引 getColumnIndex（）：根据该列中对象的名称返回该列对象的索引
                        String queryValues = cursor.getString(cursor.getColumnIndex(key));
                        if (value.equals(queryValues)) {
                            closeDb();
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "查询是否包含的error：" + e.getMessage());
                e.printStackTrace();
            }
            closeDb();
        }
        return false;
    }

    /**
     * @return 根据字段查询整个列中的数据
     */
    public List<JsonObject> queryList(String[] columns) {
        ArrayList<JsonObject> list = null;
        if (columns != null && columns.length > 0) {
            // 查询数据
            /**
             * String table, :表名
             * String[] columns, ：查询哪些列 ,如果传null代表查询所有列
             * String selection, ：查询的条件
             * String[] selectionArgs, ：条件占位符的值
             * String groupBy, ：按什么分组
             * String having,String orderBy ：按什么排序
             */

            try {
                cursor = getSqlDataBase().query(mTableName, columns, null, null, null, null, null);

                // 4:解析cursor对象 getCount:返回结果集中的行数，如果为空的话就不必去查询了
                if (cursor != null && cursor.getCount() > 0) {
                    list = new ArrayList<>();
                    // 下一行是否还有数据 moveToNext：如果往后面移动返回就为true，否则就是数据没有了
                    while (cursor.moveToNext()) {
                        // 根据列中对象的名称返回该列对象的索引 getColumnIndex（）：根据该列中对象的名称返回该列对象的索引
                        JsonObject object = new JsonObject();

                        for (int i = 0; i < columns.length; i++) {
                            // 查询的列
                            String column = columns[i];
                            // 列中对应的数据
                            String queryValue = cursor.getString(cursor.getColumnIndex(column));
                            object.addProperty(column, queryValue);
                        }
                        list.add(object);
                    }
                }
            } catch (Exception e) {
                LogUtil.e("查询数据库列错误：" + e.getMessage());
            } finally {
                closeDb();
            }
        }
        return list;
    }

    /**
     * @param whereClause 需要修改的列
     * @param whereArgs   需要修改列对应的值
     * @param whereKey    修改的条件，也就是哪个根据哪个字段来进行的修改
     * @param whereValue  修改条件的key的value
     * @return
     */
    public boolean update(List<String> whereClause, List<String> whereArgs, String whereKey, String whereValue) {
        if ((whereClause != null) && (whereClause.size() > 0) && (whereArgs != null) && (whereArgs.size() == whereClause.size())) {
            // database.execSQL("update tableName set phone=? where name=?;", new
            // Object[]{userInfoBean.phone, userInfoBean.name});

            // 创建sql
            String sql = "update " + mTableName + " set ";

            for (int i = 0; i < whereClause.size(); i++) {
                String key = whereClause.get(i);
                if (!TextUtils.isEmpty(key) && !key.equals(whereKey)) {
                    if (i != whereClause.size() - 1) {
                        sql += (key + "=?,");
                    } else {
                        sql += (key + "=? where " + whereKey + "=?;");
                    }
                }
            }
            try {
                whereArgs.add(whereArgs.size(), whereValue);
                String[] strings = ConvertUtil.ListToStringArray(whereArgs);
                getSqlDataBase().execSQL(sql, strings);
                closeDb();
                return true;
            } catch (Exception e) {
                LogUtil.e(TAG, "修改数据库错误：" + e.getMessage());
                e.printStackTrace();
            }
            closeDb();
        }
        return false;
    }

    /**
     * @param whereClause 指定的唯一id
     * @param whereArgs   id的值
     * @return 每次删除一条对象
     */
    public int delete(String whereClause, String whereArgs) {
        if ((!TextUtils.isEmpty(whereClause)) && (!TextUtils.isEmpty(whereArgs))) {
            int deleteId = 0;
            try {
                deleteId = getSqlDataBase().delete(mTableName, (whereClause + "=?"), new String[]{whereArgs});

            } catch (Exception e) {
                LogUtil.e(TAG, "删除数据库信息错误：" + e.getMessage());
                e.printStackTrace();
            }
            closeDb();
            return deleteId;
        }
        return 0;
    }

    /**
     * 删除表中所有的数据
     */
    public void deleteAll() {
        try {
            getSqlDataBase().delete(mTableName, null, null);
            LogUtil.e(TAG, "删除所有的行成功！");
        } catch (Exception e) {
            LogUtil.e(TAG, "删除所有的行：" + e.getMessage());
        }
        closeDb();

    }

    /**
     * @return 获取数据库对象
     */
    private synchronized SQLiteDatabase getSqlDataBase() {
        if ((mDataBase == null) || (!mDataBase.isOpen())) {
            if (sqliteHelper != null) {
                mDataBase = sqliteHelper.getReadableDatabase();
            }
        }
        return mDataBase;
    }

    /**
     * 关闭数据库
     */
    private void closeDb() {
        getSqlDataBase().close();
        if (cursor != null) {
            boolean closed = cursor.isClosed();
            if (!closed) {
                cursor.close();
            }
        }
    }
}
