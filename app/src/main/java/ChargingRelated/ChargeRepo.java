package ChargingRelated;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChargeRepo {
    
    private ChargeDao chargeDao;
    
    public ChargeRepo(Application application){
        ChargeDatabase database = ChargeDatabase.getInstance(application);
        chargeDao = database.ChargeDao() ;
    }

    public void insertCharge(final ChargeEntity.Charging charging){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargeDao.insertCharge(charging);
            }
        });
        executorService.shutdown();
    }

    public void updateCharge(final ChargeEntity.Charging charging){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargeDao.updateCharge(charging);
            }
        });
        executorService.shutdown();
    }

    public LiveData<ChargeEntity.Charging> getCharge(String transactionId){
        return chargeDao.getCharge(transactionId) ;
    }

    public void insertCost(final ChargeEntity.Cost cost){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargeDao.insertCost(cost);
            }
        });
        executorService.shutdown();

    }
    public void updateCost(final ChargeEntity.Cost cost){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargeDao.updateCost(cost);
            }
        });
        executorService.shutdown();
    }

    public LiveData<ChargeEntity.Cost> getCost(String transactionId){
        return chargeDao.getCost(transactionId) ;
    }

}
