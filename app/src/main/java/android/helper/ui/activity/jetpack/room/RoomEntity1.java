package android.helper.ui.activity.jetpack.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * room的表
 * 1：使用@Entity 标记是一个room数据库的表文件
 * 2：使用@Primarkey设置主键，autoGenerate = true： 主键自增长
 * 3：columnInfo(name = "value") 修改列名
 * 4：@Ignore :忽略不写入表中
 */
@Entity(tableName = "room_table_1")
public class RoomEntity1 {

    @PrimaryKey
    private long id;
    private String createTime;

    private String name;
    private int age;

    @ColumnInfo(name = "six")
    private int six;

    @Ignore
    private boolean isMaster;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    @Override
    public String toString() {
        return "RoomEntity1{" +
                "id=" + id +
                ", createTime='" + createTime + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", six=" + six +
                ", isMaster=" + isMaster +
                '}';
    }
}
