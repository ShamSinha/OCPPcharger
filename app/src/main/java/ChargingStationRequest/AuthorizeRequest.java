package ChargingStationRequest;

import AuthorizationRelated.IdTokenType;

import org.json.JSONException;
import org.json.JSONObject ;



public class AuthorizeRequest {

    public static JSONObject payload() throws JSONException {
        JSONObject jo  = new JSONObject();

        jo.put("idToken", IdTokenType.getp());

        return jo ;
    }

}
