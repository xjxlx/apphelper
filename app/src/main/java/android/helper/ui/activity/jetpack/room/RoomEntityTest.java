package android.helper.ui.activity.jetpack.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "room_test")
public class RoomEntityTest {

    @ColumnInfo
    @PrimaryKey
    @NonNull
    private String uid = "";

    @ColumnInfo
    private int six = 0;

    @ColumnInfo
    @NonNull
    private String name = "";

    @ColumnInfo(name = "CURRENT_TIMESTAMP")
    @NonNull
    private String createTime = "";

    @ColumnInfo
    @NonNull
    private String time = "";

    @NotNull
    public String getTime() {
        return time;
    }

    public void setTime(@NotNull String time) {
        this.time = time;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @NotNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NotNull String uid) {
        this.uid = uid;
    }

    public int getSix() {
        return six;
    }

    public void setSix(int six) {
        this.six = six;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return "RoomEntityTest{" +
                "uid='" + uid + '\'' +
                ", six=" + six +
                ", name='" + name + '\'' +
                ", createTime='" + createTime + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
