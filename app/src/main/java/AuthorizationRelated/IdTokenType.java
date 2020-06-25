package AuthorizationRelated;

import org.json.JSONException;
import org.json.JSONObject;

import EnumDataType.IdTokenEnumType;

public class IdTokenType {

    public static String idToken = null;
    public static IdTokenEnumType type = null ;

    public static void setIdToken(String idToken) {
        IdTokenType.idToken = idToken;
    }
    public static void setType(IdTokenEnumType type) {
        IdTokenType.type = type;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject jp  = new JSONObject();

        jp.put("idToken",IdTokenType.idToken);
        jp.put("type", IdTokenType.type.toString()) ;
        if(IdTokenType.type == IdTokenEnumType.KeyCode) {
           jp.put("additionalInfo", AdditionalInfoType.getp());
        }
        return jp;
    }
}
