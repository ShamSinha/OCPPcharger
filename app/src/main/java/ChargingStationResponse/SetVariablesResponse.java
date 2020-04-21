package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import DataType.SetVariableResultType;

public class SetVariablesResponse {

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("setVariableResult", SetVariableResultType.getp());
        return jo;
    }
}
