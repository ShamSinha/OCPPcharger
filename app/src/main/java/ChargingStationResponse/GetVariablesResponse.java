package ChargingStationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DataType.GetVariableResultType;

public class GetVariablesResponse {

    public JSONObject payload(JSONArray list) throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("getVariableResult", list);
        return jo;
    }
}
