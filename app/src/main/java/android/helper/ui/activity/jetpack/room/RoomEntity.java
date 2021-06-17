package android.helper.ui.activity.jetpack.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.android.helper.base.BaseEntity;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * 1：创建一个数据库的表，供数据库使用,使用@Entity（）标记这各类是一个数据库的表
 * 1.1：表名如果不手动写的话，就会用本类的名字作为表名
 * </p>
 * <p>
 * 2：创建数据库的主键 PrimaryKey
 * <p>
 * <p>
 * 3:创建数据库的字段
 * </p>
 */
@Entity(tableName = "table_test")
public class RoomEntity extends BaseEntity {

    // 每个数据库表里面的类上面必须要有一个主键，默认是不会自动生成的，autoGenerate 主键的值是否由Room自动生成,默认false
    @PrimaryKey
    @NonNull
    private String id = "";

    // 数据库中的字段，如果说希望字段名字和数据库中存储的字段名字不一样，则可以使用@columnInfo去指定名字
    @ColumnInfo(name = "test_name")
    @NonNull
    private String name = "";

    // 不使用指定名字作为表名
    @NonNull
    private String realName = "";

    // 忽略的字段，不会在数据库中生成对应的列
    @Ignore()
    private int six;

    public RoomEntity() {
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getRealName() {
        return realName;
    }

    public void setRealName(@NotNull String realName) {
        this.realName = realName;
    }

    public int getSix() {
        return six;
    }

    public void setSix(int six) {
        this.six = six;
    }

    @NotNull
    @Override
    public String toString() {
        return "RoomEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", realName='" + realName + '\'' +
                ", six=" + six +
                '}';
    }
}
