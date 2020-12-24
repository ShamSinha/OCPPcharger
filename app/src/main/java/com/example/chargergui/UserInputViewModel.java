package com.example.chargergui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import AuthorizationRelated.IdTokenRepo;
import Controller_Components.ControllerRepo;
import SOCDisplayRelated.InputEntity;
import SOCDisplayRelated.InputRepo;
import SOCDisplayRelated.SocRepo;

public class UserInputViewModel extends AndroidViewModel {

    private InputRepo inputRepo ;
    private IdTokenRepo idTokenRepo;
    private SocRepo socRepo ;
    private ControllerRepo controllerRepo;
    double tariff = 6 ;// Rs. per KWh

    public UserInputViewModel(@NonNull Application application) {
        super(application);
        inputRepo = new InputRepo(application);
        idTokenRepo = new IdTokenRepo(application);
        controllerRepo = new ControllerRepo(application);
        socRepo = new SocRepo(application);
    }

    public String getCurrency(){
        return controllerRepo.getController("TariffCostCtrlr","Currency").getvalue();
    }

    public double getSoc(){
        return socRepo.getSoc().getValue().getInitialSOC();
    }

    public void inputCharge(float charge, int amount){
        reset();
        inputRepo.insert(new InputEntity(amount,charge));
    }

    public void reset(){
        if(!inputRepo.isEmptyInputTable())  inputRepo.deleteAll();
    }
    public void fullCharge(){
        reset();
        int amount = estimatedCost(100);
        inputRepo.insert(new InputEntity(amount,100));
    }

    public double estimatedCharge(int amount){

        int BatteryCapacity = socRepo.getSoc().getValue().getBatteryCapacity() ; //  Wh
        double initSOC = socRepo.getSoc().getValue().getInitialSOC() ;  // < 100

        double energyRequired = amount*1000/tariff  ; // Wh

        double energyTarget = energyRequired + initSOC*BatteryCapacity/100 ;

        return energyTarget*100/BatteryCapacity;
    }

    public int estimatedCost(double TargetSOC){
        double InitialSOC = socRepo.getSoc().getValue().getInitialSOC() ;
        int BatteryCapacity = socRepo.getSoc().getValue().getBatteryCapacity() ; //  Wh
        double energy = (TargetSOC - InitialSOC)*BatteryCapacity/100000 ;  // KWh ;

        return (int) Math.round(energy*tariff);
    }

    public int getMaximumValidAmount(){
        return estimatedCost(100) ;
    }





}
