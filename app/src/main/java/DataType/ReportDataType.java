package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDataType {

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("component",ComponentType.name);
        jp.put("variable",VariableType.name);
        jp.put("variableAttribute", VariableAttributeType.getp());
        jp.put("variableCharacteristics", VariableCharacteristicsType.getp());

        return jp;
    }

}
