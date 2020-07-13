package DataType;

import ChargingStationDetails.ChargingStationStates;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.ChargingStateEnumType;
import EnumDataType.ReasonEnumType;

public class TransactionType {

    public static String transactionId ;
    public static ChargingStateEnumType chargingState ;
    public static int timeSpentCharging ;
    public static ReasonEnumType stoppedReason ;
    public static int remoteStartId ;

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("transactionId", TransactionType.transactionId);

        if(TransactionType.chargingState != null) {
            jp.put("chargingState", TransactionType.chargingState.toString());
        }

        if(TransactionType.timeSpentCharging != 0) {
            jp.put("timeSpentCharging", TransactionType.timeSpentCharging);
        }
        if(TransactionType.stoppedReason != null) {
            jp.put("stoppedReason",TransactionType.stoppedReason.toString());
        }
        if (TransactionType.remoteStartId != 0){
            jp.put("remoteStartId",TransactionType.remoteStartId);
        }
        return jp ;
    }

}
