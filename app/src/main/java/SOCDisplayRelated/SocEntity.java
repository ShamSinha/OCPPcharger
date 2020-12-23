package SOCDisplayRelated;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SocEntity {
    
    @PrimaryKey(autoGenerate = true)
    private int id;

    public float InitialSOC;
    public int BatteryCapacity;
    public String BatteryType;

    public SocEntity(float initialSOC, int batteryCapacity, String batteryType) {
        InitialSOC = initialSOC;
        BatteryType = batteryType;
        BatteryCapacity = batteryCapacity;
    }

    public int getId() {
            return id;
        }

    public void setId(int id) {
        this.id = id;
    }

    public float getInitialSOC() {
        return InitialSOC;
    }

    public String getBatteryType() {
        return BatteryType;
    }
    public int getBatteryCapacity() {
        return BatteryCapacity;
    }

    }


