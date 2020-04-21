package Controller_Components;


import java.util.List;

public class TxCtlr {

    public static boolean ChargingBeforeAcceptedEnabled; //allow charging before accepting BootNotificationResponse with Registration status Accepted
    public static int EVConnectionTimeOut = 300 ; // in seconds
    public static boolean StopTxOnEVSideDisconnect;
    public static boolean TxBeforeAcceptedEnabled;
    public static int MaxEnergyOnInvalidId; // in Wh
    public static boolean StopTxOnInvalidId;
    public enum TxStartPoint{
        EVConnected , Authorized
    }
    public enum TxStopPoint{
        EVConnected
    }


}


