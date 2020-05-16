package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.RequestStartStopStatusEnumType;

public class RequestStartTransactionResponse {
    private static RequestStartStopStatusEnumType status ;
    private static String transactionId ;

    public static RequestStartStopStatusEnumType getStatus() {
        return status;
    }

    public static void setStatus(RequestStartStopStatusEnumType status) {
        RequestStartTransactionResponse.status = status;
    }

    public static String getTransactionId() {
        return transactionId;
    }

    public static void setTransactionId(String transactionId) {
        RequestStartTransactionResponse.transactionId = transactionId;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", status.toString());
        jo.put("transactionId" , transactionId);
        return jo;
    }

}
