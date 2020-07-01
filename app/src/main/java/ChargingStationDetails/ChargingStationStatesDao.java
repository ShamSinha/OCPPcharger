package ChargingStationDetails;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

public interface ChargingStationStatesDao {

    @Insert
    public void insert(ChargingStationStates chargingStationStates) ;

    @Query("UPDATE ChargingStationStates SET isEVSideCablePluggedIn = :state WHERE transactionId = :transactionId ")
    public void updateEVSideCablePluggedIn(String transactionId , boolean state);

    @Query("UPDATE ChargingStationStates SET isAuthorized = :state WHERE transactionId = :transactionId")
    public void updateAuthorized(String transactionId , boolean state);

    @Query("UPDATE ChargingStationStates SET isDataSigned = :state WHERE transactionId = :transactionId ")
    public void updateDataSigned(String transactionId , boolean state);

    @Query("UPDATE ChargingStationStates SET isPowerPathClosed = :state WHERE transactionId = :transactionId")
    public void updatePowerPathClosed(String transactionId , boolean state);

    @Query("UPDATE ChargingStationStates SET isEnergyTransfer = :state WHERE transactionId = :transactionId")
    public void updateEnergyTransfer(String transactionId , boolean state);



    @Query("SELECT isEVSideCablePluggedIn FROM ChargingStationStates WHERE transactionId = :transactionId ")
    public LiveData<Boolean> isEVSideCablePluggedIn(String transactionId);

    @Query("SELECT isAuthorized FROM ChargingStationStates WHERE transactionId = :transactionId")
    public LiveData<Boolean> isAuthorized(String transactionId );


    @Query("SELECT isDataSigned FROM ChargingStationStates WHERE transactionId = :transactionId ")
    public LiveData<Boolean> isDataSigned(String transactionId );

    @Query("SELECT isPowerPathClosed FROM ChargingStationStates WHERE transactionId = :transactionId")
    public LiveData<Boolean> isPowerPathClosed(String transactionId );

    @Query("SELECT isEnergyTransfer FROM ChargingStationStates WHERE transactionId = :transactionId")
    public LiveData<Boolean> isEnergyTransfer(String transactionId );

    @Query("DELETE FROM ChargingStationStates")
    public void deleteStates() ;

}
