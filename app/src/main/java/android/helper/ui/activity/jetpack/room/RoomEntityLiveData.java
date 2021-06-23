package android.helper.ui.activity.jetpack.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * room的表
 * 1：使用@Entity 标记是一个room数据库的表文件
 * 2：使用@Primarkey设置主键，autoGenerate = true： 主键自增长
 * 3：columnInfo(name = "value") 修改列名
 * 4：@Ignore :忽略不写入表中
 */
@Entity(tableName = "room_table_live_data")
public class RoomEntityLiveData {

    @PrimaryKey
    private long id;

    @NonNull
    private String createTime;

    @NonNull
    private String name;

    private int age;

    @ColumnInfo(name = "six")
    private int six;

    @Ignore
    private boolean isMaster;

    public RoomEntityLiveData() {
        name = "";
        createTime = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(@NotNull String createTime) {
        this.createTime = createTime;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSix() {
        return six;
    }

    public void setSix(int six) {
        this.six = six;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    @NotNull
    @Override
    public String toString() {
        return "RoomEntity2{" +
                "id=" + id +
                ", createTime='" + createTime + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", six=" + six +
                ", isMaster=" + isMaster +
                '}';
    }
}
