package DataType;

import org.json.JSONException;
import org.json.JSONObject;

public class UnitOfMeasureType {
    public static String unit = "kWh";
    public static int multiplier = 1 ;


    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("unit" , unit ) ;
        jp.put("multiplier", multiplier) ;

        return jp ;
    }

}
