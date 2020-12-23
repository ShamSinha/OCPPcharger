package ChargingRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ChargingStationDetails.ChargingStationStatesRepo;
import Controller_Components.Controller;
import Controller_Components.ControllerRepo;
import TransactionRelated.TransactionEntities;
import TransactionRelated.TransactionEventRepo;

public class ChargeViewModel extends AndroidViewModel {
    private TransactionEventRepo eventRepo ;
    private ControllerRepo controllerRepo ;
    private ChargingStationStatesRepo chargingStationStatesRepo ;
    private String transactionId ;

    public ChargeViewModel(@NonNull Application application) {
        super(application);
        eventRepo = new TransactionEventRepo(application);
        controllerRepo = new ControllerRepo(application);
        chargingStationStatesRepo = new ChargingStationStatesRepo(application) ;
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
    public boolean isStopTxOnEVSideDisconnect(){
        return controllerRepo.getController("TxCtrlr", "StopTxOnEVSideDisconnect").getvalue().equals("true");
    }

    public int getEVConnectionTimeOut(){
        return Integer.parseInt(controllerRepo.getController("TxCtrlr","EVConnectionTimeOut").getvalue()) ;
    }

    public LiveData<Boolean> isEVSideCablePluggedIn(){
        return chargingStationStatesRepo.isEVSideCablePluggedIn(transactionId);
    }

    public LiveData<Boolean> isAuthorized(){
        return chargingStationStatesRepo.isAuthorized(transactionId) ;
    }
    public LiveData<Boolean> isPowerPathClosed(){
        return chargingStationStatesRepo.isPowerPathClosed(transactionId) ;
    }

    public LiveData<Boolean> isEnergyTransfer(){
        return chargingStationStatesRepo.isEnergyTransfer(transactionId) ;
    }
    public void updatePowerPathClosed(boolean a){
        chargingStationStatesRepo.updatePowerPathClosed(transactionId,a);
    }
    public void updateEnergyTransfer(boolean b) {
        chargingStationStatesRepo.updateEnergyTransfer(transactionId,b);
    }
















}
