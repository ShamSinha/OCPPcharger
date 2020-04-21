package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.DataEnumType;

public class VariableCharacteristicsType {
    public static String unit ;
    public static DataEnumType dataType ;
    public static float minLimit ;
    public static float maxLimit ;

    public static void setUnit(String unit) {
        VariableCharacteristicsType.unit = unit;
    }

    public static void setDataType(DataEnumType dataType) {
        VariableCharacteristicsType.dataType = dataType;
    }

    public static void setMinLimit(float minLimit) {
        VariableCharacteristicsType.minLimit = minLimit;
    }

    public static void setMaxLimit(float maxLimit) {
        VariableCharacteristicsType.maxLimit = maxLimit;
    }
    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("unit",unit);
        jp.put("datatype",dataType.toString());
        jp.put("minLimit",minLimit);
        jp.put("maxLimit",maxLimit);
        return jp;
    }
}
