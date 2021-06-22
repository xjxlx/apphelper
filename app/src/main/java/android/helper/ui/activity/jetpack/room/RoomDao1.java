package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * room数据库的操作方法
 * 1:使用@Dao标记为一个room数据库的操作类
 * 2:该类必须是一个接口或者抽象类
 */
@Dao
public interface RoomDao1 {

    // 返回的主键的id
    @Insert
    long roomInsert(RoomEntity1 roomEntity1);

    @Delete
    int roomDelete(RoomEntity1 roomEntity1);

    @Update()
    int roomUpdate(RoomEntity1 roomEntity1);

    @Query("select * from room_table_1 where id =:id")
    RoomEntity1 roomQuery(long id);

    @Query("select * from room_table_1")
    List<RoomEntity1> roomQuery();

}
