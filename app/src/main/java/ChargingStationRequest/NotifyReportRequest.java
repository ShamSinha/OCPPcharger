package ChargingStationRequest;

import com.example.chargergui.dateTime2;

import org.json.JSONException;
import org.json.JSONObject;

import DataType.ReportDataType;

public class NotifyReportRequest {
    public static int requestId;
    public static String generatedAt;
    public static int seqNo = 0;
    public static ReportDataType reportData;

    public static void setRequestId(int requestId) {
        NotifyReportRequest.requestId = requestId;
    }

    public static void setGeneratedAt() {
        dateTime2 dT = new dateTime2();
        NotifyReportRequest.generatedAt = dT.dateTime();
    }

    public static void setSeqNo() {
        NotifyReportRequest.seqNo = NotifyEventRequest.seqNO + 1 ;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("requestId", requestId);
        jo.put("generatedAt", generatedAt );
        jo.put("seqNo", seqNo);
        jo.put("reportData", ReportDataType.getp());

        return jo;
    }
}