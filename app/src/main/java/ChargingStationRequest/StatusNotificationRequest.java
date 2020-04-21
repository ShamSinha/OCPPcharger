package ChargingStationRequest;

import DataType.EVSEType;
import EnumDataType.ConnectorStatusEnumType;
import com.example.chargergui.dateTime2;

import org.json.JSONException;
import org.json.JSONObject;


public class StatusNotificationRequest {
    public static String timestamp ;
    public static ConnectorStatusEnumType connectorStatus = ConnectorStatusEnumType.Available ;

    public static void setTimestamp() {
        dateTime2 dateTime2 = new dateTime2();
        timestamp = dateTime2.dateTime();
    }

    public static void setConnectorStatus(ConnectorStatusEnumType connectorStatus) {
        StatusNotificationRequest.connectorStatus = connectorStatus;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("timestamp", StatusNotificationRequest.timestamp);
        jo.put("connectorStatus",StatusNotificationRequest.connectorStatus );
        jo.put("evseId", EVSEType.id);
        jo.put("connectorId",EVSEType.connectorId);
        return jo;
    }

}
