package DisplayMessagesRelated;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyDisplayMessagesRequest {

    private static int requestId ;
    private static boolean tbc ;

    public static int getRequestId() {
        return requestId;
    }

    public static void setRequestId(int requestId) {
        NotifyDisplayMessagesRequest.requestId = requestId;
    }

    public static boolean isTbc() {
        return tbc;
    }

    public static void setTbc(boolean tbc) {
        NotifyDisplayMessagesRequest.tbc = tbc;
    }


    public static JSONObject payload(JSONObject messageInfo) throws JSONException {
        JSONObject j = new JSONObject();
        j.put("requestId" , getRequestId()) ;
        j.put("tbc" , isTbc());
        j.put("messageInfo", messageInfo);

        return j;
    }
}
