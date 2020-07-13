package DataType;

import EnumDataType.EventTriggerEnumType;

import com.example.chargergui.dateTime2;

import org.json.JSONException;
import org.json.JSONObject;

public class EventDataType {
    public static int eventId ;
    public static String timestamp ;
    public static String actualValue ;
    public static EventTriggerEnumType trigger ;

    public static void setEventId(int eventId) {
        EventDataType.eventId = eventId;
    }

    public static void setTimestamp() {
        dateTime2 dT = new dateTime2();
        EventDataType.timestamp = dT.dateTime();
    }

    public static void setActualValue(String actualValue) {
        EventDataType.actualValue = actualValue;
    }

    public static void setTrigger(EventTriggerEnumType trigger) {
        EventDataType.trigger = trigger;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("eventId",String.valueOf(EventDataType.eventId));
        jp.put("timestamp",EventDataType.timestamp);
        jp.put("actualValue",EventDataType.actualValue);
        jp.put("trigger",EventDataType.trigger.toString());
        return jp;
    }
}
