package DataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import EnumDataType.AttributeEnumType;

import EnumDataType.SetVariableStatusEnumType;

public class SetVariableResultType {

    private AttributeEnumType attributeType ;
    private SetVariableStatusEnumType attributeStatus ;
    private ComponentType component ;
    private VariableType variable ;

    public SetVariableResultType(AttributeEnumType attributeType, SetVariableStatusEnumType attributeStatus, ComponentType component, VariableType variable) {
        this.attributeType = attributeType;
        this.attributeStatus = attributeStatus;
        this.component = component;
        this.variable = variable;
    }

    public JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();
        jp.put("attributeType",this.attributeType.toString());
        jp.put("attributeStatus", this.attributeStatus.toString());
        jp.put("component", this.component.getp()) ;
        jp.put("variable",this.variable.getp());

        return jp;
    }
}
