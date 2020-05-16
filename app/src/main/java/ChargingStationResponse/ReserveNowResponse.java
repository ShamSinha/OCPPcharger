package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.ReserveNowStatusEnumType;

public class ReserveNowResponse {
    public static ReserveNowStatusEnumType status ;

    public static ReserveNowStatusEnumType getStatus() {
        return status;
    }

    public static void setStatus(ReserveNowStatusEnumType status) {
        ReserveNowResponse.status = status;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", status.toString());
        return jo;
    }
}
