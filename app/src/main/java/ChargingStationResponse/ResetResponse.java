package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.ResetStatusEnumType;

public class ResetResponse {
    public static ResetStatusEnumType status = ResetStatusEnumType.Accepted ;

    public static void setStatus(ResetStatusEnumType status) {
        ResetResponse.status = status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", ResetResponse.status.toString());

        return jo;
    }
}
