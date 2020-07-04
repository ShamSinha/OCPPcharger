package com.example.chargergui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import AuthorizationRelated.IdTokenEntities;
import AuthorizationRelated.IdTokenRepo;
import ChargingStationDetails.ChargingStationStatesRepo;

public class Authorization1ViewModel extends AndroidViewModel {
    private IdTokenRepo repo ;
    private ChargingStationStatesRepo chargingStationStatesRepo ;

    public Authorization1ViewModel(@NonNull Application application) {
        super(application);
        repo = new IdTokenRepo(application) ;
    }

    public void insertIdTokenInfo(IdTokenEntities.IdTokenInfo idTokenInfo){
        repo.insertIdTokenInfo(idTokenInfo);
    }

    public void insertIdToken(IdTokenEntities.IdToken idToken){
        repo.insertIdToken(idToken);
    }

    public void updateIdToken(IdTokenEntities.IdToken idToken){
        repo.updateIdToken(idToken);
    }

    public void updateIdTokenInfo(IdTokenEntities.IdTokenInfo idTokenInfo){
        repo.updateIdTokenInfo(idTokenInfo);
    }

    public IdTokenEntities.IdTokenAndInfo getIdTokenAndInfo(String transactionId) {
        return repo.getIdTokenAndInfo(transactionId);
    }
    public void updateEVSideCablePluggedIn(String transactionId ,boolean state){
        chargingStationStatesRepo.updateEVSideCablePluggedIn(transactionId,state);
    }

    public void updateAuthorized(String transactionId , boolean state){
        chargingStationStatesRepo.updateAuthorized(transactionId,state);
    }

    public LiveData<Boolean> isEVSideCablePluggedIn(String transactionId){
        return chargingStationStatesRepo.isEVSideCablePluggedIn(transactionId);
    }
    public LiveData<Boolean> isAuthorized(String transactionId){
        return chargingStationStatesRepo.isAuthorized(transactionId) ;
    }
    public void deleteIdToken(String transactionId){
        repo.deleteIdToken(transactionId);
    }
    public void deleteStates(){
        chargingStationStatesRepo.deleteStates();
    }


}
