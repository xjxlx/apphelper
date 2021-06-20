package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * room数据库的操作方法
 * 1:使用@Dao标记为一个room数据库的操作类
 * 2:该类必须是一个接口或者抽象类
 */
@Dao
public interface RoomDao2 {

    @Insert
    long roomInsert(RoomEntity2 roomEntity2);

    @Delete
    void roomDelete(RoomEntity2 roomEntity2);

    @Update
    void roomUpdate(RoomEntity2 roomEntity2);

    @Query("select * from room_table_2 where id =:id")
    RoomEntity2 roomQuery(long id);

}
