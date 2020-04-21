package DataType;

import com.example.chargergui.dateTime2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.JarFile;

public class MeterValueType {
    public static String timestamp ;


    public static void setTimestamp() {
        dateTime2 d = new dateTime2();
        timestamp = d.dateTime();
    }

   /* public static JSONObject samplevalue(JSONArray ja) throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("sampledValue", ja ) ;
        return jp ;
    }
    public static JSONObject timestamp() throws JSONException {
        JSONObject j = new JSONObject();
        j.put("timestamp",timestamp);
        return j;
    }*/

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("timestamp",timestamp);
        jp.put("sampledValue", SampledValueType.getp() ) ;
        return jp;
    }


}
