package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

@Dao
public interface RoomDaoTest {

    @Insert(entity = RoomEntityTest.class)
    long insert(RoomEntityTest entity3);

    @Delete
    int delete(RoomEntityTest test);

}
