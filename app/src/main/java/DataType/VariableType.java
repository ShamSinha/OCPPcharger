package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class VariableType {

    public String name ;

    public VariableType(String name) {
        this.name = name;
    }

    public JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("name",this.name ) ;
        return jp;
    }
}
