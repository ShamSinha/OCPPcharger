package DataType;

import EnumDataType.MessageFormatEnumType;

public class MessageContentType {

    private MessageFormatEnumType format ;
    private String language  ;
    private String content ;


    public void setFormat(MessageFormatEnumType format) {
        this.format = format;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageFormatEnumType getFormat() {
        return format;
    }

    public String getLanguage() {
        return language;
    }

    public String getContent() {
        return content;
    }
}
