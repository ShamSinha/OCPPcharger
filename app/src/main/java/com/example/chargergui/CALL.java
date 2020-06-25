package com.example.chargergui;

import org.json.JSONObject;

import java.util.UUID;


public class CALL extends WebsocketMessage{

    private static String Action ;
    private final JSONObject Payload ;
    private static String MessageId ;

    public CALL(String Action,JSONObject Payload){
        CALL.Action = Action ;
        this.Payload = Payload ;
    }

    public static String getMessageId() {
        return MessageId;
    }

    public static void setMessageIdIfCallHasToSent() {
        MessageId = UUID.randomUUID().toString();
    }

    public static void setMessageIdIfCallReceived(String messageId) {
        MessageId = messageId;
    }

    public static String getAction() {
        return Action;
    }

    public JSONObject getPayload() { return this.Payload ;}


}