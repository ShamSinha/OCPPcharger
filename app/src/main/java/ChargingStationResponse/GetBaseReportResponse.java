package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.GenericDeviceModelStatusEnumType;

public class GetBaseReportResponse {
    public static GenericDeviceModelStatusEnumType status = GenericDeviceModelStatusEnumType.Accepted;

    public static void setStatus(GenericDeviceModelStatusEnumType status) {
        GetBaseReportResponse.status = status;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", GetBaseReportResponse.status.toString());
        return jo;
    }
}
