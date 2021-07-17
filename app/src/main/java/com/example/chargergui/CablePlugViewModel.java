package com.example.chargergui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ChargingStationDetails.ChargingStationStatesRepo;
import Controller_Components.ControllerRepo;

public class CablePlugViewModel extends AndroidViewModel {
    private ControllerRepo controllerRepo ;
    private ChargingStationStatesRepo chargingStationStatesRepo ;

    public  CablePlugViewModel(@NonNull Application application) {
        super(application);
        controllerRepo = new ControllerRepo(application);
        chargingStationStatesRepo = new ChargingStationStatesRepo(application);
    }
    public int getEVConnectionTimeOut(){
        return Integer.parseInt(controllerRepo.getController("TxCtrlr","EVConnectionTimeOut").getvalue()) ;
    }

    public void updateEVSideCablePluggedIn(boolean state){
        chargingStationStatesRepo.updateEVSideCablePluggedIn(transactionId,state);
    }
}
