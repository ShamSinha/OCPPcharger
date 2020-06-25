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

    public MessageInfoType(int id , String priority, String state, String startDateTime, String endDataTime, String transactionId) {
        MessageInfoType.id = id ;
        MessageInfoType.priority = priority ;
        MessageInfoType.state = state ;
        MessageInfoType.startDateTime = startDateTime ;
        MessageInfoType.endDataTime = endDataTime ;
        MessageInfoType.transactionId  = transactionId ;
    }

    public static int getId() {
        return id;
    }

    public static String getPriority() {
        return priority;
    }

    public static String getState() {
        return state;
    }

    public static String getStartDateTime() {
        return startDateTime;
    }

    public static String getEndDataTime() {
        return endDataTime;
    }

    public static String getTransactionId() {
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
