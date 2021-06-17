package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * room数据库的操作类，使用 @Dao注解，作为一个标记，表明他是一个数据库的操作类
 */
@Dao
public interface RoomDao {

    // 创建查询方法，可以插入多个参数
    @Insert
    void roomInsert(RoomEntity... roomEntity);

    // 删除方法
    @Delete
    void roomDelete(RoomEntity entity);

    // 修改方法
    @Update
    void roomUpdate(RoomEntity entity);

    // 查询方法
    @Query("SELECT * FROM TABLE_TEST WHERE id in (:userId)")
    RoomEntity roomQuery(String userId);

    // 查询所有
    @Query("SELECT * FROM table_test")
    List<RoomEntity> roomQueryList();

}
