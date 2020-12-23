package AuthorizationRelated;

public class MessageContent {

    public String format;
    public String language;
    public String content;

    public void setFormat(String format) {
        this.format = format;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormat() {
        return format;
    }

    public String getLanguage() {
        return language;
    }

    public String getContent() {
        return content;
    }
}