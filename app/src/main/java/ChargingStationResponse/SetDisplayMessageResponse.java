package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import DisplayMessagesRelated.DisplayMessageStatusEnumType;

public class SetDisplayMessageResponse {

    public static DisplayMessageStatusEnumType status ;

    public static void setStatus(DisplayMessageStatusEnumType status) {
        SetDisplayMessageResponse.status = status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status",SetDisplayMessageResponse.status.toString());
        return jo;
    }
}
