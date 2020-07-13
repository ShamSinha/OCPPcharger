package DataType;

import com.example.chargergui.dateTime2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.jar.JarFile;

public class MeterValueType {
    public static String timestamp ;

    public static void setTimestamp() {
        dateTime2 d = new dateTime2();
        timestamp = d.dateTime();
    }

    public static JSONObject getp(JSONArray sampledValues) throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("timestamp",timestamp);

        jp.put("sampledValue", sampledValues) ;
        return jp;
    }


}
