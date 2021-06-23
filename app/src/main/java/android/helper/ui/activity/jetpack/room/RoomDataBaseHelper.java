package android.helper.ui.activity.jetpack.room;

import android.helper.app.App;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * room 数据库的管理类
 * 1:必须使用@Database 去注解
 * 2：必须是抽象类，且继承RoomDatabase
 * 3:version: 版本号，entities:对应的实体类
 */
@Database(version = 2, entities = {RoomEntity1.class, RoomEntity2.class,
        RoomEntityLiveData.class, RoomEntityTest.class})
public abstract class RoomDataBaseHelper extends RoomDatabase {

    private static final String mDdName = "room_table.db";
    private static volatile RoomDataBaseHelper INSTANCE;

    abstract RoomDao1 getDao1();

    abstract RoomDao2 getDao2();

    abstract RoomDaoLiveData getLiveData();

    abstract RoomDaoTest getDaoTest();

    /**
     * @return 获取room数据库的实例，这个对象应该是单利的一个对象，不应该多次去重复获取
     */
    public static RoomDataBaseHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (RoomDataBaseHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(
                                    App.getInstance().getApplicationContext(), // 上下文
                                    RoomDataBaseHelper.class, // 继承了RoomDatabase的类
                                    mDdName // 数据库db的名字
                            )
                            // .allowMainThreadQueries()// 在UI线程中运行
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
