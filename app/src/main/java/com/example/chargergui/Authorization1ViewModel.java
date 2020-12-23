package com.example.chargergui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import AuthorizationRelated.IdTokenEntity;
import AuthorizationRelated.IdTokenInfoEntity;
import AuthorizationRelated.IdTokenRepo;
import ChargingStationDetails.ChargingStationStatesRepo;

public class Authorization1ViewModel extends AndroidViewModel {
    private IdTokenRepo repo ;
    private ChargingStationStatesRepo chargingStationStatesRepo ;
    private String transactionId ;

    public Authorization1ViewModel(@NonNull Application application) {
        super(application);
        repo = new IdTokenRepo(application) ;
        chargingStationStatesRepo = new ChargingStationStatesRepo(application);
    }

    public void insertIdTokenInfo(IdTokenInfoEntity idTokenInfo){
        repo.insertIdTokenInfo(idTokenInfo);
    }

    public void insertIdToken(IdTokenEntity idToken){
        repo.insertIdToken(idToken);
    }

    public void updateIdToken(IdTokenEntity idToken){
        repo.updateIdToken(idToken);
    }

    public void updateIdTokenInfo(IdTokenInfoEntity idTokenInfo){
        repo.updateIdTokenInfo(idTokenInfo);
    }

    public IdTokenEntity getIdToken() {
        return repo.getIdToken();
    }

    public IdTokenInfoEntity getIdTokenInfo() {
        return repo.getIdTokenInfo();
    }

    public void updateEVSideCablePluggedIn(boolean state){
        chargingStationStatesRepo.updateEVSideCablePluggedIn(transactionId,state);
    }

    public LiveData<Boolean> isEVSideCablePluggedIn(){
        return chargingStationStatesRepo.isEVSideCablePluggedIn(transactionId);
    }
    public LiveData<Boolean> isAuthorized(){
        return chargingStationStatesRepo.isAuthorized(transactionId) ;
    }
    public void deleteIdToken(){
        repo.deleteIdToken();
    }
    public void deleteStates(){
        chargingStationStatesRepo.deleteStates();
    }

}
