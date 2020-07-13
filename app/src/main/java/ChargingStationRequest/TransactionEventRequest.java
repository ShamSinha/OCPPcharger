package ChargingStationRequest;


import com.example.chargergui.dateTime2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import DataType.EVSEType;
import AuthorizationRelated.IdTokenType;
import DataType.MeterValueType;
import DataType.SampledValueType;
import DataType.TransactionType;
import EnumDataType.ChargingStateEnumType;
import TransactionRelated.TriggerReasonEnumType;
import TransactionRelated.TransactionEventEnumType;

public class TransactionEventRequest {

    public static TransactionEventEnumType eventType ;
    public static String timestamp ;
    public static TriggerReasonEnumType triggerReason ;
    public static long SeqNo;
    public static Boolean offline;
    public static float cableMaxCurrent;  //in Ampere
    public static JSONObject meterValues ;

    public static void setTimestamp() {
        dateTime2 d = new dateTime2();
        timestamp = d.dateTime();
    }

    public static void SetSeqNo(long seqNo){
        SeqNo = seqNo ;
    }

    public static void SetMeterValues(JSONArray sampledValues) throws JSONException {
        meterValues = MeterValueType.getp(sampledValues) ;
    }
    
    public static JSONObject payload() throws JSONException {
        JSONObject jo  = new JSONObject();

        jo.put("eventType",eventType.toString());
        jo.put("timestamp",timestamp);
        jo.put("triggerReason",TransactionEventRequest.triggerReason.toString());
        jo.put("seqNo",SeqNo);
        jo.put("offline",offline);
        jo.put("cableMaxCurrent",cableMaxCurrent);
        jo.put("transactionInfo", TransactionType.getp());
        if(TransactionEventRequest.triggerReason == TriggerReasonEnumType.Authorized || TransactionEventRequest.triggerReason == TriggerReasonEnumType.Deauthorized) {
            jo.put("idToken", IdTokenType.getp());
        }
        if(EVSEType.id != 0) {
            jo.put("evse", EVSEType.getp());
        }
        if( TransactionType.chargingState == ChargingStateEnumType.Charging || TransactionType.chargingState == ChargingStateEnumType.SuspendedEVSE ){
            jo.put("meterValue" , meterValues);
        }
        return jo ;
    }

}
