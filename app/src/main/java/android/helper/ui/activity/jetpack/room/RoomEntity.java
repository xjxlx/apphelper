package android.helper.ui.activity.jetpack.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 一:room的表结构
 *
 * <p>
 * 1：@primaryKey构造一个主键，autoGenerate = true ,主键自动增长，默认为false
 * 2：columnInfo：修改列名
 * 3：@Ignore：忽略列名
 * 4:tableName:修改表名，否则就是奔类的对象
 * </p>
 *
 * <p>
 * 注意：
 * * 1：每个对象，都必须不能为null
 * * 2：每个表中，都必须要有一个主键
 * </p>
 */

@Entity(tableName = "room_entity")
public class RoomEntity {

    // 主键
    @PrimaryKey(autoGenerate = true)
    private long id;

    // 修改列名
    @NonNull
    @ColumnInfo(name = "test_name")
    private String name;

    // 不去生成
    @Ignore
    private int six;

    // 不修改列名
    private boolean isMater;

    private long time;

    public RoomEntity() {
        name = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getSix() {
        return six;
    }

    public void setSix(int six) {
        this.six = six;
    }

    public boolean isMater() {
        return isMater;
    }

    public void setMater(boolean mater) {
        isMater = mater;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RoomEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", six=" + six +
                ", isMater=" + isMater +
                ", time=" + time +
                '}';
    }
}
