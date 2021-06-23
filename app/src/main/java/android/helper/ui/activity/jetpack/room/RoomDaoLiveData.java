package android.helper.ui.activity.jetpack.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomDaoLiveData {

    @Insert(entity = RoomEntityLiveData.class)
    long roomInsert(RoomEntityLiveData data);

    @Delete
    int roomDelete(RoomEntityLiveData data);

    @Delete
    int roomDelete(List<RoomEntityLiveData> data);

    @Update
    int roomUpdate(RoomEntityLiveData data);

    @Query("select * from room_table_live_data where id = :id")
    RoomEntityLiveData roomQuery(long id);

    @Query("SELECT * FROM ROOM_TABLE_LIVE_DATA")
    LiveData<RoomEntityLiveData> roomQueryRxjava();

}
