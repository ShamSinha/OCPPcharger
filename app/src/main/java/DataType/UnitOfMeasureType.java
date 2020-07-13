package DataType;

import org.json.JSONException;
import org.json.JSONObject;

public class UnitOfMeasureType {
    public String unit ;
    public int multiplier ;

    public UnitOfMeasureType(String unit, int multiplier) {
        this.unit = unit;
        this.multiplier = multiplier;
    }

    public JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("unit" , this.unit ) ;
        jp.put("multiplier", this.multiplier) ;

        return jp ;
    }

}
