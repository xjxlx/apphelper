package android.helper.ui.activity.jetpack.room.room2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_table_11")
public class RoomTable1 {

    @PrimaryKey
    private long id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    boolean isWanMan;

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

    public boolean isWanMan() {
        return isWanMan;
    }

    public void setWanMan(boolean wanMan) {
        isWanMan = wanMan;
    }

    @Override
    public String toString() {
        return "RoomTable1{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isWanMan=" + isWanMan +
                '}';
    }
}
