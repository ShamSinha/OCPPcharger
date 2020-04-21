package ChargingStationRequest;

import DataType.EventDataType;
import com.example.chargergui.dateTime2;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyEventRequest {
    public static String generatedAt ;
    public static Boolean tbc ;
    public static int seqNO = 0;

    private static void setGeneratedAt() {
        dateTime2 dt = new dateTime2();
        NotifyEventRequest.generatedAt = dt.dateTime();
    }

    public static void setTbc(Boolean tbc) {
        NotifyEventRequest.tbc = tbc;
    }

    public static void setSeqNO() {
        NotifyEventRequest.seqNO = NotifyEventRequest.seqNO + 1;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("generatedAt", NotifyEventRequest.generatedAt );
        jo.put("tbc",NotifyEventRequest.tbc);
        jo.put("seqNO", NotifyEventRequest.seqNO);
        jo.put("eventData", EventDataType.getp());

        return jo;
    }
}
