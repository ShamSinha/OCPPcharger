package ChargingStationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.DataTransferStatusEnumType;

public class DataTransferResponse {
    private DataTransferStatusEnumType status ;

    public DataTransferResponse(DataTransferStatusEnumType status) {
        this.status = status;
    }

    private DataTransferStatusEnumType getStatus() {
        return status;
    }
    public JSONObject payload() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("status", this.getStatus().toString());
        return jo;
    }
}
