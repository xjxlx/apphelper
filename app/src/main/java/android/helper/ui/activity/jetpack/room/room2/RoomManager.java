package android.helper.ui.activity.jetpack.room.room2;

import android.helper.app.App;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

@Database(version = RoomManager.ROOM_VERSION, entities = {RoomTable1.class, RoomTable2.class})
public abstract class RoomManager extends RoomDatabase {

    public static final int ROOM_VERSION = 13;

    private static volatile RoomManager INSTANCE;
    private static volatile RoomManager INSTANCE2;

    public static RoomManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RoomManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            App.getInstance().getApplicationContext(),
                            RoomManager.class,
                            "room_table_2.db"
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
