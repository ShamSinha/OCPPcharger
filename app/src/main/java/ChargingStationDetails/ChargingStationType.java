package ChargingStationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import DataType.ModemType;

public class ChargingStationType {
    
    public static String serialNumber ;
    public static String model ;
    public static String vendorName ;
    public static String firmwareVersion ;

    public static void setSerialNumber(String serialNumber){
        ChargingStationType.serialNumber = serialNumber;
    }

    public static void setModel(String model) {
        ChargingStationType.model = model;
    }

    public static void setVendorName(String vendorName) {
        ChargingStationType.vendorName = vendorName;
    }

    public static void setFirmwareVersion(String firmwareVersion) {
        ChargingStationType.firmwareVersion = firmwareVersion;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("serialNumber",ChargingStationType.serialNumber);
        jp.put("model",ChargingStationType.model);
        jp.put("vendorName", ChargingStationType.vendorName) ;
        jp.put("firmwareVersion",ChargingStationType.firmwareVersion);
        jp.put("modem",ModemType.getp());
        return jp;
    }
}
