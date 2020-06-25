package DisplayMessagesRelated;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageContentType {
    private static MessageFormatEnumType format;
    private static String language;
    private static String content;

    public static void setFormat(MessageFormatEnumType format) {
        MessageContentType.format = format;
    }

    public static void setLanguage(String language) {
        MessageContentType.language = language;
    }

    public static void setContent(String content) {
        MessageContentType.content = content;
    }

    private static MessageFormatEnumType getFormat() {
        return format;
    }

    private static String getLanguage() {
        return language;
    }

    private static String getContent() {
        return content;
    }

    public static JSONObject getp() throws JSONException {
        JSONObject j = new JSONObject();
        j.put("format", getFormat().name());
        j.put("language", getLanguage());
        j.put("content", getContent());

        return j;
    }
}