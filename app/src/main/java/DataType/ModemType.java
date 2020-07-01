package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import ChargingStationDetails.ChargingStationType;

public class ModemType {
    public static String iccid;
    public static String imsi ;

    public static void setIccid(String iccid) {
        ModemType.iccid = iccid;
    }

    public static void setImsi(String imsi) {
        ModemType.imsi = imsi;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("iccid",ModemType.iccid);
        jp.put("imsi",ModemType.imsi);
        return jp;
    }
}
