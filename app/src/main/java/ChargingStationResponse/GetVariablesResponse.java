package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import DataType.GetVariableResultType;

public class GetVariablesResponse {

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("getVariableResult", GetVariableResultType.getp());
        return jo;
    }
}
