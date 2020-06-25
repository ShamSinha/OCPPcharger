package ChargingStationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DataType.SetVariableResultType;

public class SetVariablesResponse {

    public static JSONObject payload(JSONArray list) throws JSONException {

        JSONObject jo = new JSONObject();

        jo.put("setVariableResult", list);
        return jo;
    }
}
