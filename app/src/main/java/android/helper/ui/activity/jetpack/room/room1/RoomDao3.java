package android.helper.ui.activity.jetpack.room.room1;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface RoomDao3 {

    @Insert(entity = RoomEntity3.class)
    long insert(RoomEntity3 entity3);
}
