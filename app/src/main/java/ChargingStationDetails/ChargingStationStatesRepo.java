package ChargingStationDetails;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChargingStationStatesRepo {
    private ChargingStationStatesDao chargingStationStatesDao ;

    public ChargingStationStatesRepo(Context context){
        ChargingStationStatesDatabase database = ChargingStationStatesDatabase.getInstance(context);
        chargingStationStatesDao = database.chargingStationStatesDao() ;
    }

    public LiveData<Boolean> isEVSideCablePluggedIn(String transactionId){
        return chargingStationStatesDao.isEVSideCablePluggedIn(transactionId);
    }
    public LiveData<Boolean> isAuthorized(String transactionId){
        return chargingStationStatesDao.isAuthorized(transactionId) ;
    }
    public LiveData<Boolean> isDataSigned(String transactionId){
        return chargingStationStatesDao.isDataSigned(transactionId) ;
    }
    public LiveData<Boolean> isPowerPathClosed(String transactionId){
        return chargingStationStatesDao.isPowerPathClosed(transactionId);
    }
    public LiveData<Boolean> isEnergyTransfer(String transactionId){
        return chargingStationStatesDao.isEnergyTransfer(transactionId);
    }

    public void deleteStates(){
        chargingStationStatesDao.deleteStates();
    }

    public void insert(final ChargingStationStates chargingStationStates){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationStatesDao.insert(chargingStationStates);
            }
        });
        executorService.shutdown();
    }

    public void updateEVSideCablePluggedIn(final String transactionId , final boolean state){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
               chargingStationStatesDao.updateEVSideCablePluggedIn(transactionId,state);
            }
        });
        executorService.shutdown();
    }

    public void updateAuthorized(final String transactionId , final boolean state){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationStatesDao.updateAuthorized(transactionId,state);
            }
        });
        executorService.shutdown();
    }

    public void updateDataSigned(final String transactionId , final boolean state){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationStatesDao.updateDataSigned(transactionId,state);
            }
        });
        executorService.shutdown();
    }

    public void updatePowerPathClosed(final String transactionId , final boolean state){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationStatesDao.updatePowerPathClosed(transactionId,state);
            }
        });
        executorService.shutdown();
    }
    public void updateEnergyTransfer(final String transactionId , final boolean state){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationStatesDao.updateEnergyTransfer(transactionId,state);
            }
        });
        executorService.shutdown();
    }
}
