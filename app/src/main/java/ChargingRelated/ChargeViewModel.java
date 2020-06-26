package ChargingRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChargeViewModel extends AndroidViewModel {
    private ChargeRepo chargeRepo ;

    public ChargeViewModel(@NonNull Application application) {
        super(application);
        chargeRepo = new ChargeRepo(application);
    }

    public void insertCharge(ChargeEntity.Charging charging){
        chargeRepo.insertCharge(charging);
    }
    public void updateCharge(ChargeEntity.Charging charging){
        chargeRepo.updateCharge(charging);
    }
    public void insertCost(ChargeEntity.Cost cost){
        chargeRepo.insertCost(cost);
    }
    public void updateCost(ChargeEntity.Cost cost){
        chargeRepo.updateCost(cost);
    }
    public LiveData<ChargeEntity.Charging> getCharge(String transactionId){
        return chargeRepo.getCharge(transactionId) ;
    }

    public LiveData<ChargeEntity.Cost> getCost(String transactionId){
        return chargeRepo.getCost(transactionId) ;
    }


}
