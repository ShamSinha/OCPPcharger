package Controller_Components;

import EnumDataType.MessageFormatEnumType;
import EnumDataType.MessagePriorityEnumType;

public class DisplayMessageCtrlr {
    public static boolean Enabled;
    public static boolean Available;
    public static int DisplayMessages ; //Amount of different messages that are currently configured in this Charging Station, via SetDisplayMessageRequest.
    public static int PersonalMessageSize ;
    public static MessageFormatEnumType SupportedFormats;
    public static MessagePriorityEnumType SupportedPriorities ;
}
