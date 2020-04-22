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

    public static boolean ChargingBeforeAcceptedEnabled; //allow charging before accepting BootNotificationResponse with Registration status Accepted
    public static int EVConnectionTimeOut = 300 ; // in seconds
    public static boolean StopTxOnEVSideDisconnect = false;
    public static boolean TxBeforeAcceptedEnabled;
    public static int MaxEnergyOnInvalidId; // in Wh
    public static boolean StopTxOnInvalidId ;
    public enum TxStartPoint{
        EVConnected , Authorized
    }
    public enum TxStopPoint{
        EVConnected
    }

}


