package ChargingRelated;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;



public interface ChargeDao {

    @Insert
    public void insertCharge(ChargeEntity.Charging charging);

    @Update
    public void updateCharge(ChargeEntity.Charging charging);

    @Query("SELECT * FROM ChargeEntity.Charging WHERE transactionId = :transactionId")
    LiveData<ChargeEntity.Charging> getCharge(String transactionId);

    @Insert
    public void insertCost(ChargeEntity.Cost cost) ;

    @Query("UPDATE ChargeEntity.Cost SET TotalCost = :cost  WHERE transactionId = :transactionId")
    public void updateCost(float cost , String transactionId) ;

    @Query("SELECT TotalCost FROM ChargeEntity.Cost WHERE transactionId = :transactionId")
    LiveData<Float> getTotalCost(String transactionId);


}
