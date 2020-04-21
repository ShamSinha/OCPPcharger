package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class VariableType {

    public static String name ;

    public static void setName(String name) {
        VariableType.name = name;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("name",VariableType.name ) ;

        return jp;
    }
}
