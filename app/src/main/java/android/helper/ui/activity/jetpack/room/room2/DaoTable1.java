package android.helper.ui.activity.jetpack.room.room2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DaoTable1 {

    @Insert(entity = RoomTable1.class, onConflict = OnConflictStrategy.REPLACE)
    long insert(RoomTable1 roomTable1);

    @Delete
    int delete(RoomTable1 roomTable1);

    @Update
    int update(RoomTable1 roomTable1);

    @Query("select * from room_table_11 where id ==:id ")
    RoomTable1 query(long id);

}
