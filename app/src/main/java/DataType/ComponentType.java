package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import DataType.EVSEType;

public class ComponentType {
    public static String name ;

    public static void setName(String name) {
        ComponentType.name = name;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("name",ComponentType.name ) ;
        return jp;
    }
}
