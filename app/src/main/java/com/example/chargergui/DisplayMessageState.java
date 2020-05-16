package com.example.chargergui;

import EnumDataType.MessageStateEnumType;

public class DisplayMessageState {

    private static MessageStateEnumType messageState ;

    public static void setMessageState(MessageStateEnumType messageState) {
        DisplayMessageState.messageState = messageState;
    }

    public static MessageStateEnumType getMessageState() {
        return messageState;
    }
}
