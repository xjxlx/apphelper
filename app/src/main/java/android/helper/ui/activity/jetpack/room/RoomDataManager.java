package android.helper.ui.activity.jetpack.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * 三：数据库的操作类
 * <p>
 * 1：操作数据库的类，明确规定了，必须要是一个abstract类型的类
 * 2：该类必须要继承 RoomDatabase
 * 3：使用@Database注解，添加表名
 * </p>
 */
@Database(version = 1, exportSchema = true, entities = RoomEntity.class)
public abstract class RoomDataManager extends RoomDatabase {

    public static final String DATA_BASE_FILE_NAME = "test_room.db";

    public abstract RoomDao getDao();

}
