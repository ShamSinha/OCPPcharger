package com.example.chargergui;

public class ChargingStationStates {

    public static boolean isCablePluggedIn= false ;
    public static boolean isAuthorized = false;
    public static boolean isDataSigned =false ;
    public static boolean isPowerPathClosed =false;
    public static boolean isEnergyTransfer = false;

    public static void setCablePluggedIn(boolean cablePluggedIn) {
        isCablePluggedIn = cablePluggedIn;
    }

    public static void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public static void setDataSigned(boolean dataSigned) {
        isDataSigned = isCablePluggedIn && isAuthorized && dataSigned;
    }

    public static void setPowerPathClosed(boolean powerPathClosed) {
        isPowerPathClosed = isCablePluggedIn && isAuthorized && isDataSigned && powerPathClosed;
    }

    public static void setEnergyTransfer(boolean energyTransfer) {
        isEnergyTransfer = isCablePluggedIn && isAuthorized && isDataSigned && isPowerPathClosed && energyTransfer;
    }



}
