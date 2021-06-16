package android.helper.ui.activity.jetpack.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Room数据库的操作工具
 */
@Database(entities = {RoomEntity.class}, version = 1, exportSchema = true)
public abstract class RoomData extends RoomDatabase {

    private static RoomData dataUtil;

//    public static RoomDataUtil getInstance() {
//        if (dataUtil == null) {
//            dataUtil = Room.databaseBuilder(
//                    App.getInstance(),
//                    RoomDataUtil.class,
//                    "RoomEntity"
//            ).build();
//        }
//        return dataUtil;
//    }

//    public RoomDao getDao(){
//        return dataUtil.
//    }

    public abstract RoomDao getDao();
}
