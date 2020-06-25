package charging_viewmodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Charge {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public float Voltage ;
    public float Current ;
    public float SOC ;
    public float TimeSpent ;
    public float Cost ;

    public Charge(float voltage, float current, float SOC, float timeSpent, float cost) {
        Voltage = voltage;
        Current = current;
        this.SOC = SOC;
        TimeSpent = timeSpent;
        Cost = cost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public float getVoltage() {
        return Voltage;
    }

    public float getCurrent() {
        return Current;
    }

    public float getSOC() {
        return SOC;
    }

    public float getTimeSpent() {
        return TimeSpent;
    }

    public float getCost() {
        return Cost;
    }
}
