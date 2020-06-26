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

    @Query("SELECT * FROM ChargeType.Charging WHERE transactionId = :transactionId")
    LiveData<ChargeEntity.Charging> getCharge(String transactionId);

    @Insert
    public void insertCost(ChargeEntity.Cost cost) ;

    @Update
    public void updateCost(ChargeEntity.Cost cost) ;

    @Query("SELECT * FROM ChargeType.Cost WHERE transactionId = :transactionId")
    LiveData<ChargeEntity.Cost> getCost(String transactionId);



}