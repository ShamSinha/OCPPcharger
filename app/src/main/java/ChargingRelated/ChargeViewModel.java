package ChargingRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import Controller_Components.Controller;
import Controller_Components.ControllerRepo;
import TransactionRelated.TransactionEntities;
import TransactionRelated.TransactionEventRepo;

public class ChargeViewModel extends AndroidViewModel {
    private TransactionEventRepo eventRepo ;
    private ControllerRepo controllerRepo ;

    public ChargeViewModel(@NonNull Application application) {
        super(application);
        eventRepo = new TransactionEventRepo(application);
        controllerRepo = new ControllerRepo(application);
    }

    public LiveData<TransactionEntities.EventRequestAndResponse> getEventReqAndResp(){
        return eventRepo.getEventReqAndResp();
    }

    public Controller getController(String component , String variable){
        return controllerRepo.getController(component , variable) ;
    }
    public String getCurrency(){
        return controllerRepo.getController("TariffCostCtrlr","Currency").getvalue() ;
    }

















}
