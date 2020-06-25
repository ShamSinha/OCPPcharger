package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import DataType.EVSEType;

public class ComponentType {
    private String name ;

    public ComponentType(String name) {
        this.name = name;
    }

    public JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("name",this.name ) ;
        return jp;
    }
}
