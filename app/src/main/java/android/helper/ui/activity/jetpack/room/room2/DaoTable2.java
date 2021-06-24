package android.helper.ui.activity.jetpack.room.room2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DaoTable2 {

    @Insert(entity = RoomTable2.class, onConflict = OnConflictStrategy.REPLACE)
    long insert(RoomTable2 table2);

    @Delete
    int delete(RoomTable2 table2);

    @Update
    int update(RoomTable2 table2);

    @Query("select * from room_table_22 where id ==:id ")
    RoomTable2 query(long id);
}
