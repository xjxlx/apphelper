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
    public boolean roomInsert(RoomEntity... roomEntity);

    // 删除方法
    @Delete
    public boolean roomDelete();

    // 修改方法
    @Update
    public boolean roomUpdate();

    // 查询方法
    @Query("")
    public RoomEntity roomQuery();

    // 查询所有
    @Query("")
    public List<RoomEntity> roomQueryList();

}
