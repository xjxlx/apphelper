package com.android.helper.utils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.common.utils.LogUtil;

/**
 * Created by Administrator on 2019/3/26.
 */

public class SQLiteManager extends SQLiteOpenHelper {

    private static String mSQL;// 具体的sql语句
    private final String TAG = "SQLiteManager";

    /**
     * 构造方法
     * (Context context, String name, CursorFactory factory,int version)
     * 数据库创建的构造方法 数据库名称 sql_table.db ，版本号为1
     *
     * @param context  上下文对象
     * @param /name    数据库名称 secb.db
     * @param /factory 游标工厂
     * @param /version 数据库版本
     */
    private SQLiteManager(Context context, @NonNull SQLEntity sqlEntity) {
        // context :上下文 name： 数据库文件的名称 factory:用来创建cursor对象，默认传null
        // version:数据库的版本,从android4.0之后只能升不能降。
        // 数据库的版本号， 这个版本号只能增长，不能倒退
        super(context, (sqlEntity.getTableName() + ".db"), null, sqlEntity.getVersionCode());
    }
    /**
     * + "sing_id text, " --------------->查询的唯一码
     * + "mp3_name text, " -------------->音乐的名字
     * + "mp3_url text, " ---------------->音乐的url
     * + "mp3_image_url text, " --------------> 音乐图片的url
     * + "type text, " ----------------> 下载的类型
     * + "levele text, " -----------------> level等级
     * + "unit_name text, " ----------------->单元的名字
     * + "creat_time text)"; ------------------>创建的时间
     */
    // public static final String CREATE_MUSIC = "create table " + "ABC" + " ("
    // + "id integer primary key autoincrement, "
    // + "sing_id text, "
    // + "mp3_name text, "
    // + "mp3_url text, "
    // + "mp3_image_url text, "
    // + "type text, "
    // + "levele text, "
    // + "unit_name text, "
    // + "unit_type text, "
    // + "creat_time text)";

    /**
     * @param sqlEntity 实体对象
     * @return 指定一个数据库对象的名字，返回一个数据库的管理对象
     */
    public static synchronized SQLiteManager getInstance(Context context, SQLEntity sqlEntity) {
        return new SQLiteManager(context, sqlEntity);
    }

    /**
     * 数据库第一次被使用时创建数据库
     * 初始化数据库的表结构
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.e(TAG, " onCreate--->创建数据库");
        LogUtil.e(TAG, "onCreate：获取到的Sql语句为：" + mSQL);
        try {
            if (!TextUtils.isEmpty(mSQL)) {
                db.execSQL(mSQL);
                LogUtil.e(TAG, "onCreate：数据库创建成功！");
            } else {
                LogUtil.e("需要创建的数据库表为空!");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate：数据库创建失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 数据库版本升级时调用
     * 数据库版本发生改变时才会被调用,数据库在升级时才会被调用;
     *
     * @param db         操作数据库
     * @param oldVersion 旧版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.e(TAG, " onUpgrade--->更新数据库");
        if (oldVersion >= newVersion) {
            return;
        }
        try {
            LogUtil.e(TAG, "获取更新数据库的Sql：" + mSQL);
            if (!TextUtils.isEmpty(mSQL)) {
                db.execSQL(mSQL);
                LogUtil.e(TAG, "更新数据库成功！");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "更新数据库失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
