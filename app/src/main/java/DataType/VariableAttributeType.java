package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.AttributeEnumType;
import EnumDataType.MutabilityEnumType;

public class VariableAttributeType {
    public static AttributeEnumType type ;
    public static String value ;
    public static MutabilityEnumType mutability ;
    public static boolean persistent ;
    public static boolean constant ;

    public static void setType(AttributeEnumType type) {
        VariableAttributeType.type = type;
    }

    public static void setValue(String value) {
        VariableAttributeType.value = value;
    }

    public static void setMutability(MutabilityEnumType mutability) {
        VariableAttributeType.mutability = mutability;
    }

    public static void setPersistent(boolean persistent) {
        VariableAttributeType.persistent = persistent;
    }

    public static void setConstant(boolean constant) {
        VariableAttributeType.constant = constant;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("type",type.toString());
        jp.put("value",value);
        jp.put("mutability",mutability.toString());
        jp.put("persistent",persistent);
        jp.put("constant",constant);
        return jp;
    }
}
