package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.SetNetworkProfileStatusEnumType;

public class SetNetworkProfileResponse {

    public static SetNetworkProfileStatusEnumType status ;

    public static void setStatus(SetNetworkProfileStatusEnumType status) {
        SetNetworkProfileResponse.status = status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo  = new JSONObject();
        jo.put("status", SetNetworkProfileResponse.status.toString()) ;
        return jo;
    }

}
