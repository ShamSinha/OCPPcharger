package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.ChangeAvailabilityStatusEnumType;

public class ChangeAvailabilityResponse {

    private static ChangeAvailabilityStatusEnumType status = ChangeAvailabilityStatusEnumType.Accepted;

    public static void setStatus(ChangeAvailabilityStatusEnumType status) {
        ChangeAvailabilityResponse.status = status;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", ChangeAvailabilityResponse.status.toString());
        return jo;
    }
}
