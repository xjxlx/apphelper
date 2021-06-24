package android.helper.ui.activity.jetpack.room.room2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "room_table_22")
public class RoomTable2 {

    @PrimaryKey
    long id;

    @ColumnInfo
    String name;

    @ColumnInfo
    boolean isMaser;

    @ColumnInfo
    int age;

    @ColumnInfo
    String name2;

    @ColumnInfo
    @NotNull
    String name3 = "";

    String name4;

    @NotNull
    String name5 = "";

    @NotNull
    String name6 = "666";

    int age1;
    int age2;
    boolean isLast;
    String name7 = "";

    @NotNull
    String name8 = "";

    @NotNull
    public String getName5() {
        return name5;
    }

    public void setName5(@NotNull String name5) {
        this.name5 = name5;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    @NotNull
    public String getName3() {
        return name3;
    }

    public void setName3(@NotNull String name3) {
        this.name3 = name3;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMaser() {
        return isMaser;
    }

    public void setMaser(boolean maser) {
        isMaser = maser;
    }

    @Override
    public String toString() {
        return "RoomTable2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isMaser=" + isMaser +
                ", age=" + age +
                ", name2='" + name2 + '\'' +
                ", name3='" + name3 + '\'' +
                '}';
    }
}
