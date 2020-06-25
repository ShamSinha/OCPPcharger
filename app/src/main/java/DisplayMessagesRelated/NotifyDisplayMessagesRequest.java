package DisplayMessagesRelated;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyDisplayMessagesRequest {

    private static int requestId ;
    private static boolean tbc ;
    private static MessageInfoType messageInfo ;

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

    public static MessageInfoType getMessageInfo() {
        return messageInfo;
    }

    public static void setMessageInfo(MessageInfoType messageInfo) {
        NotifyDisplayMessagesRequest.messageInfo = messageInfo;
    }

    public static JSONObject payload() throws JSONException {
        JSONObject j = new JSONObject();
        j.put("requestId" , getRequestId()) ;
        j.put("tbc" , isTbc());
        j.put("messageInfo", getMessageInfo());

        return j;
    }
}
