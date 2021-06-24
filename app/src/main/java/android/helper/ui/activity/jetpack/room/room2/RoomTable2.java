package android.helper.ui.activity.jetpack.room.room2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_table_22")
public class RoomTable2 {

    @PrimaryKey
    long id;

    @ColumnInfo
    String name;

    @ColumnInfo
    boolean isMaser;

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
                '}';
    }
}
