package com.example.chargergui;

public class ChargingStationStates {

    public static boolean isEVSideCablePluggedIn = false ;
    public static boolean isAuthorized = false;
    public static boolean isDataSigned =false ;
    public static boolean isPowerPathClosed =false;
    public static boolean isEnergyTransfer = false;

    public static void setCablePluggedIn(boolean cablePluggedIn) {
        isEVSideCablePluggedIn = cablePluggedIn;
    }

    public static void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public static void setDataSigned(boolean dataSigned) {
        isDataSigned = isEVSideCablePluggedIn && isAuthorized && dataSigned;
    }

    public static void setPowerPathClosed(boolean powerPathClosed) {
        isPowerPathClosed = isEVSideCablePluggedIn && isAuthorized && isDataSigned && powerPathClosed;
    }

    public static void setEnergyTransfer(boolean energyTransfer) {
        isEnergyTransfer = isEVSideCablePluggedIn && isAuthorized && isDataSigned && isPowerPathClosed && energyTransfer;
    }



}
