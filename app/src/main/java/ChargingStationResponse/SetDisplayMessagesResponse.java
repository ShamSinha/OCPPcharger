package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import DisplayMessagesRelated.DisplayMessageStatusEnumType;

public class SetDisplayMessagesResponse {

    public static DisplayMessageStatusEnumType status ;

    public static void setStatus(DisplayMessageStatusEnumType status) {
        SetDisplayMessagesResponse.status = status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", SetDisplayMessagesResponse.status.toString());
        return jo;
    }
}
