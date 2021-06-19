package android.helper.ui.activity.jetpack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 二：数据库的调用方法
 * 1：使用@Dao,标记为一个调用方法
 */

@Dao
public interface RoomDao {

    /**
     * 单个或者多个的插入方法
     *
     * @param roomEntities 插入的对象
     */
    @Insert
    void roomInsert(RoomEntity... roomEntities);

    /**
     * 插入一个集合
     *
     * @param list 插入的集合数据
     *             OnConflictStrategy.REPLACE: 冲突策略是取代旧数据同时继续事务。
     *             OnConflictStrategy.ROLLBACK:冲突策略是回滚事务。
     *             OnConflictStrategy.ABORT:冲突策略是终止事务。
     *             OnConflictStrategy.FAIL:冲突策略是事务失败。
     *             OnConflictStrategy.IGNORE:冲突策略是忽略冲突。
     *
     *             <p>
     *             当使用 @Insert 注解的方法仅仅只有一个参数时，可以返回一个 long 类型的值，其表示插入项的 rowId。
     *             如果参数是一个数组或集合，则返回 long[] 或 List<Long> 类型的值。
     *             </P>
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = RoomEntity.class)
    long[] roomInsert(List<RoomEntity> list);

    @Delete
    void roomDelete(RoomEntity... entity);

    @Delete
    void roomDelete(List<RoomEntity> list);

    @Update
    void roomUpdate(RoomEntity... entities);

    @Update
    void roomUpdate(List<RoomEntity> list);

    /**
     * @param id 指定的id
     * @return 根据id查询指定的数据
     */
    @Query("SELECT * FROM room_entity WHERE id =:id")
    RoomEntity querySingle(int id);

    /**
     * @return 查询整个列表的数据
     */
    @Query("select * from room_entity")
    List<RoomEntity> queryList();

}
