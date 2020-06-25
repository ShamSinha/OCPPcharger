package DisplayMessagesRelated;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageInfoType {

    private static int id;
    private static String priority;
    private static String state;
    private static String startDateTime;
    private static String endDataTime;
    private static String transactionId;

    public static void setId(int id) {
        MessageInfoType.id = id;
    }

    public static void setPriority(String priority) {
        MessageInfoType.priority = priority;
    }

    public static void setState(String state) {
        MessageInfoType.state = state;
    }

    public static void setStartDateTime(String startDateTime) {
        MessageInfoType.startDateTime = startDateTime;
    }

    public static void setEndDataTime(String endDataTime) {
        MessageInfoType.endDataTime = endDataTime;
    }

    public static void setTransactionId(String transactionId) {
        MessageInfoType.transactionId = transactionId;
    }

    private static int getId() {
        return id;
    }

    private static String getPriority() {
        return priority;
    }

    private static String getState() {
        return state;
    }

    private static String getStartDateTime() {
        return startDateTime;
    }

    private static String getEndDataTime() {
        return endDataTime;
    }

    private static String getTransactionId() {
        return transactionId;
    }


    public static JSONObject getp() throws JSONException {
        JSONObject jo  = new JSONObject();
        jo.put("id", getId() ) ;
        jo.put("priority", getPriority());
        jo.put("state",getState() ) ;
        jo.put("startDateTime",getStartDateTime() );
        jo.put("endDateTime", getEndDataTime()) ;
        jo.put("transactionId", getTransactionId());
        jo.put("message",MessageContentType.getp());

        return jo;
    }
}
