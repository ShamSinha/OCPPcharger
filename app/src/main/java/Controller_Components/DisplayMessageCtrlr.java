package Controller_Components;

import EnumDataType.MessageFormatEnumType;
import EnumDataType.MessagePriorityEnumType;

public class DisplayMessageCtrlr {
    private static boolean Enabled ;
    private static boolean Available;
    private static int DisplayMessages ; //Amount of different messages that are currently configured in this Charging Station, via SetDisplayMessageRequest.
    private static int PersonalMessageSize ;
    private static MessageFormatEnumType SupportedFormats;
    private static MessagePriorityEnumType SupportedPriorities ;

    public static boolean isEnabled() {
        return Enabled;
    }

    public static void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public static boolean isAvailable() {
        return Available;
    }

    public static void setAvailable(boolean available) {
        Available = available;
    }

    public static int getDisplayMessages() {
        return DisplayMessages;
    }

    public static void setDisplayMessages(int displayMessages) {
        DisplayMessages = displayMessages;
    }

    public static int getPersonalMessageSize() {
        return PersonalMessageSize;
    }

    public static void setPersonalMessageSize(int personalMessageSize) {
        PersonalMessageSize = personalMessageSize;
    }

    public static MessageFormatEnumType getSupportedFormats() {
        return SupportedFormats;
    }

    public static void setSupportedFormats(MessageFormatEnumType supportedFormats) {
        SupportedFormats = supportedFormats;
    }

    public static MessagePriorityEnumType getSupportedPriorities() {
        return SupportedPriorities;
    }

    public static void setSupportedPriorities(MessagePriorityEnumType supportedPriorities) {
        SupportedPriorities = supportedPriorities;
    }
}
