package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface RoomDao3 {

    @Insert
    long insert(RoomEntity3 entity3);
}
