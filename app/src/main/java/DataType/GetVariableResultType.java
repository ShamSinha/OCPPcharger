package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.AttributeEnumType;

import EnumDataType.GetVariableStatusEnumType;

public class GetVariableResultType {
    public static GetVariableStatusEnumType attributeStatus;
    public static AttributeEnumType attributeType = AttributeEnumType.Actual ;
    public static String attributeValue = null;

    public static void setAttributeStatus(GetVariableStatusEnumType attributeStatus) {
        GetVariableResultType.attributeStatus = attributeStatus;
    }

    public static void setAttributeType(AttributeEnumType attributeType) {
        GetVariableResultType.attributeType = attributeType;
    }

    public static void setAttributeValue(String attributeValue) {
        GetVariableResultType.attributeValue = attributeValue;
    }
    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("attributeStatus", GetVariableResultType.attributeStatus.toString());
        jp.put("attributeType",GetVariableResultType.attributeType.toString());
        jp.put("attributeValue", GetVariableResultType.attributeValue);
        jp.put("component", ComponentType.name) ;
        jp.put("variable",VariableType.name);
        return jp;
    }
}
