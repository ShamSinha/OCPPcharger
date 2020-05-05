package Controller_Components;


import android.content.Intent;

import com.example.chargergui.CablePlugActivity;
import com.example.chargergui.Charging;
import com.example.chargergui.ChargingStationStates;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ReasonEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class TxCtlr {

    private static boolean ChargingBeforeAcceptedEnabled; //allow charging before accepting BootNotificationResponse with Registration status Accepted
    private static int EVConnectionTimeOut = 300 ; // in seconds
    private static boolean StopTxOnEVSideDisconnect = false;
    private static boolean TxBeforeAcceptedEnabled;
    private static int MaxEnergyOnInvalidId; // in Wh
    private static boolean StopTxOnInvalidId ;
    public enum TxStartPoint{
        EVConnected , Authorized
    }
    public enum TxStopPoint{
        EVConnected
    }

    public static boolean isChargingBeforeAcceptedEnabled() {
        return ChargingBeforeAcceptedEnabled;
    }

    public static void setChargingBeforeAcceptedEnabled(boolean chargingBeforeAcceptedEnabled) {
        ChargingBeforeAcceptedEnabled = chargingBeforeAcceptedEnabled;
    }

    public static int getEVConnectionTimeOut() {
        return EVConnectionTimeOut;
    }

    public static void setEVConnectionTimeOut(int EVConnectionTimeOut) {
        TxCtlr.EVConnectionTimeOut = EVConnectionTimeOut;
    }

    public static boolean isStopTxOnEVSideDisconnect() {
        return StopTxOnEVSideDisconnect;
    }

    public static void setStopTxOnEVSideDisconnect(boolean stopTxOnEVSideDisconnect) {
        StopTxOnEVSideDisconnect = stopTxOnEVSideDisconnect;
    }

    public static boolean isTxBeforeAcceptedEnabled() {
        return TxBeforeAcceptedEnabled;
    }

    public static void setTxBeforeAcceptedEnabled(boolean txBeforeAcceptedEnabled) {
        TxBeforeAcceptedEnabled = txBeforeAcceptedEnabled;
    }

    public static int getMaxEnergyOnInvalidId() {
        return MaxEnergyOnInvalidId;
    }

    public static void setMaxEnergyOnInvalidId(int maxEnergyOnInvalidId) {
        MaxEnergyOnInvalidId = maxEnergyOnInvalidId;
    }

    public static boolean isStopTxOnInvalidId() {
        return StopTxOnInvalidId;
    }

    public static void setStopTxOnInvalidId(boolean stopTxOnInvalidId) {
        StopTxOnInvalidId = stopTxOnInvalidId;
    }
}


