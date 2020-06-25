package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.AttributeEnumType;

import EnumDataType.GetVariableStatusEnumType;

public class GetVariableResultType {
    private GetVariableStatusEnumType attributeStatus;
    private AttributeEnumType attributeType;
    private String attributeValue = null;
    private ComponentType component ;
    private VariableType variable ;

    public GetVariableResultType(GetVariableStatusEnumType attributeStatus, AttributeEnumType attributeType, String attributeValue,ComponentType component, VariableType variable) {
        this.attributeStatus = attributeStatus;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
        this.component = component;
        this.variable = variable;
    }

    public JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("attributeStatus", this.attributeStatus.toString());
        jp.put("attributeType",this.attributeType.toString());
        jp.put("attributeValue", this.attributeValue);
        jp.put("component", this.component) ;
        jp.put("variable", this.variable);
        return jp;
    }
}
