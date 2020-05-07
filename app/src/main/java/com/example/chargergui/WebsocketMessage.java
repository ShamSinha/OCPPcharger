package com.example.chargergui;


public class WebsocketMessage {

    private static int MessageTypeId;

    public static void setMessageTypeId(int messageTypeId) {
        MessageTypeId = messageTypeId;
    }

    public static int getMessageTypeId() {
        return MessageTypeId;
    }
}

