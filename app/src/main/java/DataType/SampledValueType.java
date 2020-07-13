package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.MeasurandEnumType;
import EnumDataType.ReadingContextEnumType;

public class SampledValueType {

    public double value;  // Indicates the measured value.
    public ReadingContextEnumType context;
    public MeasurandEnumType measurand ;

    public SampledValueType(double value, ReadingContextEnumType context, MeasurandEnumType measurand) {
        this.value = value;
        this.context = context;
        this.measurand = measurand;
    }

    public JSONObject getp(UnitOfMeasureType measure) throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("value", this.value) ;
        jp.put("context" , this.context.toString());
        jp.put("measurand" , this.measurand.toString());
        jp.put("unitOfMeasure" , measure.getp()) ;
        return jp ;
    }
}