package DataType;

import com.example.chargergui.ChargingStationStates;

import org.json.JSONException;
import org.json.JSONObject;

import ChargingStationRequest.TransactionEventRequest;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ReasonEnumType;
import EnumDataType.TransactionEventEnumType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionType {
    public static String transactionId = null  ;
    public static ChargingStateEnumType chargingState = null ;
    public static int timeSpentCharging = 0 ;
    public static ReasonEnumType stoppedReason = null;


    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("transactionId", TransactionType.transactionId);

        if(ChargingStationStates.isEVSideCablePluggedIn) {
            jp.put("chargingState", TransactionType.chargingState.toString());
        }
        if(TransactionType.timeSpentCharging!= 0) {
            jp.put("timeSpentCharging", TransactionType.timeSpentCharging);
        }
        if(TransactionType.stoppedReason!= null) {
            jp.put("stoppedReason",TransactionType.stoppedReason.toString());
        }
        return jp ;
    }

}
