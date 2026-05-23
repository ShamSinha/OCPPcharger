package AuthorizationRelated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdditionalInfoType {
    private static String additionalIdToken = "";
    private static String type = "";

    public static void setAdditionalIdToken(String additionalIdToken) {
        AdditionalInfoType.additionalIdToken = additionalIdToken;
    }

    public static void setType(String type) {
        AdditionalInfoType.type = type;
    }

    public static JSONArray getp() throws JSONException {
        JSONObject additionalInfo = new JSONObject();
        additionalInfo.put("additionalIdToken", additionalIdToken);
        additionalInfo.put("type", type);
        return new JSONArray().put(additionalInfo);
    }
}
