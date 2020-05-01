package ChargingStationRequest;

import EnumDataType.BootReasonEnumType;
import DataType.ChargingStationType;

import org.json.JSONException;
import org.json.JSONObject;


public class BootNotificationRequest {
    private static BootReasonEnumType reason = BootReasonEnumType.PowerUp ;

    public static void setReason(BootReasonEnumType reason) {  // use when reason is different type of PowerUp
        BootNotificationRequest.reason = reason;
    }
    public static BootReasonEnumType getReason() {
        return BootNotificationRequest.reason ;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo  = new JSONObject();
        jo.put("reason", BootNotificationRequest.reason) ;
        jo.put("chargingStation", ChargingStationType.getp());

        return jo;
    }

}

