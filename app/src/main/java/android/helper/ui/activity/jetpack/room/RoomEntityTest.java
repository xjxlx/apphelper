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
    private int six;

    @ColumnInfo
    private String name;

    @ColumnInfo(name = "CURRENT_TIMESTAMP")
    private String createTime;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoomEntity3{" +
                "uid='" + uid + '\'' +
                ", six=" + six +
                ", name='" + name + '\'' +
                '}';
    }
}
