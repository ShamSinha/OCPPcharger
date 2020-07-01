package ChargingStationDetails;

import androidx.room.Insert;
import androidx.room.Query;

public interface ChargingStationDao {

    @Insert
    public void insert(ChargingStation chargingStation);

    @Query("UPDATE ChargingStation SET firmwareVersion =:firmwareVersion")
    public void update(String firmwareVersion) ;
}
