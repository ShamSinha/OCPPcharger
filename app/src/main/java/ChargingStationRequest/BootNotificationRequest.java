package ChargingStationRequest;

import EnumDataType.BootReasonEnumType;
import ChargingStationDetails.ChargingStationType;

import org.json.JSONException;
import org.json.JSONObject;


public class BootNotificationRequest {

    private static BootReasonEnumType reason ;

    public static BootReasonEnumType getReason() {
        return reason;
    }

    public static void setReason(BootReasonEnumType reason) {
        BootNotificationRequest.reason = reason;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo  = new JSONObject();
        jo.put("reason", BootNotificationRequest.reason) ;
        jo.put("chargingStation", ChargingStationType.getp());
        return jo;
    }

}

