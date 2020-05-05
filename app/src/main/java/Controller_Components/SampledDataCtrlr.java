package Controller_Components;

public class SampledDataCtrlr {

    private static boolean Enabled ;
    private static boolean Available ;
    private static boolean SignReadings ;
    private static int TxEndedInterval ;
    public static int TxUpdatedInterval  = 60 ; // in seconds
    private static String TxEndedMeasurands = "" ;
    private static String TxStartedMeasurands = "Energy.Active.Import.Register" ;
    private static String TxUpdatedMeasurands = "Energy.Active.Import.Register" ;

    public static boolean isEnabled() {
        return Enabled;
    }

    public static void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public static boolean isAvailable() {
        return Available;
    }

    public static void setAvailable(boolean available) {
        Available = available;
    }

    public static boolean isSignReadings() {
        return SignReadings;
    }

    public static void setSignReadings(boolean signReadings) {
        SignReadings = signReadings;
    }

    public static int getTxEndedInterval() {
        return TxEndedInterval;
    }

    public static void setTxEndedInterval(int txEndedInterval) {
        TxEndedInterval = txEndedInterval;
    }

    public static int getTxUpdatedInterval() {
        return TxUpdatedInterval;
    }

    public static void setTxUpdatedInterval(int txUpdatedInterval) {
        TxUpdatedInterval = txUpdatedInterval;
    }

    public static String getTxEndedMeasurands() {
        return TxEndedMeasurands;
    }

    public static void setTxEndedMeasurands(String txEndedMeasurands) {
        TxEndedMeasurands = txEndedMeasurands;
    }

    public static String getTxStartedMeasurands() {
        return TxStartedMeasurands;
    }

    public static void setTxStartedMeasurands(String txStartedMeasurands) {
        TxStartedMeasurands = txStartedMeasurands;
    }

    public static String getTxUpdatedMeasurands() {
        return TxUpdatedMeasurands;
    }

    public static void setTxUpdatedMeasurands(String txUpdatedMeasurands) {
        TxUpdatedMeasurands = txUpdatedMeasurands;
    }
}
