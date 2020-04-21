package ChargingStationRequest;


import com.example.chargergui.dateTime2;

import org.json.JSONException;
import org.json.JSONObject;

import DataType.EVSEType;
import DataType.IdTokenType;
import DataType.MeterValueType;
import DataType.TransactionType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.TriggerReasonEnumType;
import EnumDataType.TransactionEventEnumType;

public class TransactionEventRequest {
    public static TransactionEventEnumType eventType = TransactionEventEnumType.Started  ;
    public static String timestamp ;
    public static TriggerReasonEnumType triggerReason ;
    public static int SeqNo = 0 ;
    public static Boolean offline = false;
    public static float cableMaxCurrent = 16;  //in Ampere


    public static void setTimestamp() {
        dateTime2 d = new dateTime2();
        timestamp = d.dateTime();
    }

    public static void SetSeqNo(){
        if (SeqNo < 2147483647){
            SeqNo = SeqNo + 1 ;
        }
        else {
            SeqNo = 0 ;
        }
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
        if(TransactionEventRequest.triggerReason == TriggerReasonEnumType.Authorized) {
            jo.put("idToken", IdTokenType.getp());
        }
        if(EVSEType.id != 0) {
            jo.put("evse", EVSEType.getp());
        }
        if(TransactionEventRequest.triggerReason == TriggerReasonEnumType.MeterValuePeriodic && TransactionType.chargingState == ChargingStateEnumType.Charging){
            jo.put("meterValue" , MeterValueType.getp());
        }
        return jo ;
    }


}
