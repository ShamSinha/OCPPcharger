package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class EVSEType {
    public static int id = 0;  // This contains a number (> 0) designating an EVSE of the Charging Station
    public static int connectorId  = 0; // An id to designate a specific connector (on an EVSE) by connector index number.

    public static void setId(int id) {
        EVSEType.id = id;
    }

    public static void setConnectorId(int connectorId) {
        EVSEType.connectorId = connectorId;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("id", String.valueOf(EVSEType.id));
        jp.put("connectorId", String.valueOf(EVSEType.connectorId)) ;
        return jp ;
    }
}
