package android.helper.ui.activity.jetpack.room.room2;

import android.helper.BuildConfig;
import android.helper.app.App;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.migration.Migration;

import com.android.helper.utils.room.BaseRoomManager;

@Database(version = BuildConfig.ROOM_VERSION, entities = {RoomTable1.class, RoomTable2.class})
public abstract class RoomManager extends BaseRoomManager {

    public static int VERSION = BuildConfig.ROOM_VERSION;
    public static String ROOM_DB = BuildConfig.ROOM_VERSION_FILE_NAME;

    private static volatile RoomManager INSTANCE;
    private static volatile RoomManager INSTANCE2;

    public static RoomManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RoomManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            App.getInstance().getApplicationContext(),
                            RoomManager.class,
                            ROOM_DB
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    public static RoomManager getInstance(Migration migration) {
        if (INSTANCE2 == null) {
            synchronized (RoomManager.class) {
                if (INSTANCE2 == null) {
                    INSTANCE2 = Room.databaseBuilder(
                            App.getInstance().getApplicationContext(),
                            RoomManager.class,
                            "room_table_2.db"
                    )
                            .addMigrations(migration)
                            .build();
                }
            }
        }
        return INSTANCE2;
    }

    abstract DaoTable1 getDao1();

    abstract DaoTable2 getDao2();

}
