package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.AttributeEnumType;

import EnumDataType.SetVariableStatusEnumType;

public class SetVariableResultType {
    public static AttributeEnumType attributeType ;
    public static SetVariableStatusEnumType attributeStatus ;

    public static void setAttributeType(AttributeEnumType attributeType) {
        SetVariableResultType.attributeType = attributeType;
    }

    public static void setAttributeStatus(SetVariableStatusEnumType attributeStatus) {
        SetVariableResultType.attributeStatus = attributeStatus;
    }
    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("attributeType",SetVariableResultType.attributeType.toString());
        jp.put("attributeStatus", SetVariableResultType.attributeStatus.toString());
        jp.put("component", ComponentType.name) ;
        jp.put("variable",VariableType.name);
        return jp;
    }
}
