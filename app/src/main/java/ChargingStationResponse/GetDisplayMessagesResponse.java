package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import DisplayMessagesRelated.GetDisplayMessagesStatusEnumType;

public class GetDisplayMessagesResponse {
    private static GetDisplayMessagesStatusEnumType status ;

    public static void setStatus(GetDisplayMessagesStatusEnumType status) {
        GetDisplayMessagesResponse.status = status;
    }

    public static GetDisplayMessagesStatusEnumType getStatus() {
        return status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", status.toString());
        return jo;
    }
}
