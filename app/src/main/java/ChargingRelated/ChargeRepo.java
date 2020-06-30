package ChargingRelated;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChargeRepo {
    
    private ChargeDao chargeDao;
    
    public ChargeRepo(Context context){
        ChargeDatabase database = ChargeDatabase.getInstance(context);
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
    public void updateCost(final float totalCost , final String transactionId){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargeDao.updateCost(totalCost , transactionId);
            }
        });
        executorService.shutdown();
    }

    public LiveData<Float> getCost(String transactionId){
        return chargeDao.getTotalCost(transactionId) ;
    }

}
