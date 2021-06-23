package com.android.helper.utils.room;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.room.RoomDatabase;

import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.room.RoomDeleteListener;
import com.android.helper.interfaces.room.RoomInsertListener;
import com.android.helper.interfaces.room.RoomMigrationListener;
import com.android.helper.interfaces.room.RoomQueryListener;
import com.android.helper.interfaces.room.RoomUpdateListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Room数据库的操作类，适用于不持有观察者对象的操作,适用于普通的异步操作数据库的增删改查操作
 */
public class RoomUtil {

    private static volatile RoomUtil INSTANCE;

    @Retention(RetentionPolicy.SOURCE)
    public @interface UNIT {
        /**
         * 字符串类型
         */
        String TEXT = "TEXT";
        /**
         * 数值类型
         */
        String INTEGER = "TEXT";

        /**
         * 数值类型
         */
        String INT = "INT";

        /**
         * 长整型数值
         */
        String LONG = "LONG";

        /**
         * 布尔类型
         */
        String BOOLEAN = "BOOLEAN";
    }

    public RoomUtil() {
    }

    /**
     * @return 获取了单利的对象
     */
    public static RoomUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (RoomUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RoomUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * <p>
     * 1:使用room数据库去插入一条数据局，如果成功了，就会返回插入的id，如果失败了，就返回-1
     * 2：如果使用这个方法的话，@Dao中@insert注解的方法一定要返回一个int 或者 long 类型的对象，否则无法判定是否成功了
     * </p>
     *
     * @param insertListener room数据库添加数据的回调，如果成功了就返回插入的id,如果失败了，就返回-1
     */
    @SuppressLint("CheckResult")
    public void insert(RoomInsertListener insertListener) {
        if (insertListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Long>) emitter -> {
                        try {
                            long insert = insertListener.insert();
                            emitter.onNext(insert);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Long>() {
                        @Override
                        public void onNext(Long aLong) {
                            insertListener.onResult(true, aLong, "");
                        }

                        @Override
                        public void onError(Throwable t) {
                            insertListener.onResult(false, -1, t.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * <p>
     * 1：room数据库删除一条数据，如果成功了，则返回删除条目的数量，如果失败了，则返回0
     * 2：如果使用删除的辅助类的话，那么需要在@Dao中的@delete的方法上，返回一个int 类型删除的行数，否则无法判定是否删除成功了
     * </p>
     *
     * @param deleteListener room数据库添加数据的回调
     */
    public void delete(RoomDeleteListener deleteListener) {
        if (deleteListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) emitter -> {
                        try {
                            int delete = deleteListener.delete();
                            emitter.onNext(delete);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            deleteListener.onResult(true, integer, "");
                        }

                        @Override
                        public void onError(Throwable t) {
                            deleteListener.onResult(false, 0, t.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * <p>
     * 1：使用room数据库更新一条数据，如果成功了，则返回更新数量的条目，如果更新失败了，则返回0
     * 2：如果使用这个对象去修改的话，那么需要在@Dao中@update的方法中返回一个更改影响的行数，否则无法判断
     * </p>
     *
     * @param updateListener room数据库添加数据的回调
     */
    public void update(RoomUpdateListener updateListener) {
        if (updateListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) emitter -> {
                        try {
                            int update = updateListener.update();
                            emitter.onNext(update);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }

                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            updateListener.onResult(true, integer, "");
                        }

                        @Override
                        public void onError(Throwable t) {
                            updateListener.onResult(false, 0, t.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * <p>
     * 1：使用room数据库，查询数据，根据传入的类型，返回单个或者多个数据，如果成功了，则返回对应的查询数据，
     * 如果失败了就返回null
     * 2：如果使用这个查询方法，需要在@Dao中@query的方法中返回一个数据对象，否则无法获取返回的数据
     * </p>
     *
     * @param queryListener room数据库添加数据的回调
     */
    public <T> void query(RoomQueryListener<T> queryListener) {
        if (queryListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<T>) emitter -> {

                        try {
                            T query = queryListener.query();
                            emitter.onNext(query);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }

                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST) //create方法中多了一个BackpressureStrategy类型的参数
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<T>() {
                        @Override
                        public void onNext(T t) {
                            queryListener.onResult(true, t, "");
                        }

                        @Override
                        public void onError(Throwable t) {
                            queryListener.onResult(false, null, t.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public <T extends RoomDatabase> void updateVersion(RoomMigrationListener<T> roomMigrationListener) {

    }

    /**
     * @param tableName  表名
     * @param columnName 新增列的key
     * @param unit       列的单位,TEXT、INTEGER、BOOLEAN ,建议使用枚举对象：UNIT去获取
     * @return 返回一个没有默认值的sql对象
     */
    public String addColumn(String tableName, String columnName, String unit) {
        return addColumnNotNull(tableName, columnName, unit, null);
    }

    /**
     * @param tableName    表名
     * @param columnName   新增列的key
     * @param unit         列的单位,TEXT、INTEGER、BOOLEAN ,建议使用枚举对象：UNIT去获取
     * @param defaultValue 默认的值
     * @return 返回一个有默认值且不为null的sql语句
     */
    public String addColumnNotNull(String tableName, String columnName, String unit, String defaultValue) {
        String sql = "";

        if ((!TextUtils.isEmpty(tableName)) && (!TextUtils.isEmpty(columnName)) && (!TextUtils.isEmpty(unit))) {
            sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + unit;
        }

        if (defaultValue != null) {
            sql += " NOT NULL DEFAULT " + defaultValue;
        }

        return sql;
    }

    /**
     * @param tableName      新创建的表名
     * @param primaryKey     主键的key
     * @param primaryKeyUnit 主键的单位，建议使用 UNIT.TEXT 、UNIT.INTEGER 、UNIT.INT
     * @param autoincrement  在主键为 int 或者 long 类型的时候，是否允许自增长
     * @param column         具体的参数集合，集合中的key为字段名，value为key的单位类型，如果单位中需要加入not null 的话，可以在后面进行拼接
     * @return 返回一个创建 表的sql语句
     */
    public String createTable(String tableName, String primaryKey, String primaryKeyUnit, boolean autoincrement, HashMap<String, SqlEntity> column) {
        StringBuilder sql = new StringBuilder();

        // 加入表名 和 指定主键
        if ((!TextUtils.isEmpty(tableName)) && (!TextUtils.isEmpty(primaryKey))) {
            sql.append("CREATE TABLE IF NOT EXISTS ")
                    .append(tableName)
                    .append(" ( ")
                    .append(primaryKey)
                    .append(" ")
                    .append(primaryKeyUnit)
                    .append(" PRIMARY KEY");

            // 如果主键的类型是文本类型的，那么就给他设置不能为null
            if (TextUtils.equals(primaryKeyUnit, UNIT.TEXT)) {
                sql.append(" NOT NULL");
            }
        }

        // 如果主键的类型不是文本类型，且允许自增长，那么就讲主键设置为自增长
        if ((!TextUtils.equals(primaryKeyUnit, UNIT.TEXT)) && autoincrement) {
            sql.append(" autoincrement");
        }

        sql.append(" ,");

        // 添加参数
        if ((column != null) && (column.size() > 0)) {
            Set<Map.Entry<String, SqlEntity>> entries = column.entrySet();
            for (Map.Entry<String, SqlEntity> entry : entries) {
                // 获取字段名
                String key = entry.getKey();

                SqlEntity entity = entry.getValue();
                // 单位名字
                String unit = entity.getUnit();
                // 是否允许为空
                boolean notnull = entity.isCanNot();

                // 加入字段名
                sql
                        .append(" ")
                        .append(key)
                        .append(" ")
                        .append(entry.getValue())
                ;

                // 加入非空标记
                if (!notnull) {
                    sql
                            .append(" ")
                            .append(entity.notNULL)
                            .append(" ");
                }

                // 加入逗号
                sql.append(",");
            }
        }

        // 删除最后的那个逗号
        sql.delete(0, sql.length() - 1);

        // 最后加入
        sql.append(")");

        return sql.toString();
    }

}
