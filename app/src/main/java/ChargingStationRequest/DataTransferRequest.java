package ChargingStationRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class DataTransferRequest {
    private String messageId ;
    private String vendorId ;

    public DataTransferRequest(String messageId, String vendorId) {
        this.messageId = messageId;
        this.vendorId = vendorId;
    }

    private String getMessageId() {
        return messageId;
    }

    private String getVendorId() {
        return vendorId;
    }

    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("messageId" , getMessageId());
        jo.put("vendorId",getVendorId());
        return jo;
    }
}
