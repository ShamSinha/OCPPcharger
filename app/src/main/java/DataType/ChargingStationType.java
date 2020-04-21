package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import DataType.ModemType;

public class ChargingStationType {
    public static String serialNumber = null ;
    public static String model = "SingleSocketCharger" ;
    public static String vendorName = "VendorX" ;
    public static String firmwareVersion = null ;
    public static ModemType modem = null ;

    public static void setSerialNumber(String serialNumber) {
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

    public static void setModem(ModemType modem) {
        ChargingStationType.modem = modem;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("serialNumber",ChargingStationType.serialNumber);
        jp.put("model",ChargingStationType.model);
        jp.put("vendorName", ChargingStationType.vendorName) ;
        return jp;
    }
}
