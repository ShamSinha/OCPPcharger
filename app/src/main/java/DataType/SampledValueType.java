package DataType;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.MeasurandEnumType;
import EnumDataType.ReadingContextEnumType;

public class SampledValueType {
    public static double value;  // Indicates the measured value.
    public static ReadingContextEnumType context;
    public static MeasurandEnumType measurand = MeasurandEnumType.EnergyActiveImportRegister;



    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("value", SampledValueType.value) ;
        jp.put("context" , SampledValueType.context.toString());
        jp.put("measurand" , SampledValueType.measurand);
        jp.put("unitOfMeasure" ,UnitOfMeasureType.getp()) ;
        return jp ;
    }
}