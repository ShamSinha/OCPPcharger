package com.example.chargergui;

import org.json.JSONObject;

import java.util.UUID;


public class CALLRESULT extends WebsocketMessage {

    private final JSONObject Payload ;
    private static String MessageId ;


    public CALLRESULT(JSONObject Payload){
        this.Payload = Payload ;
    }

    public static String getMessageId() {
        return MessageId;
    }

    public static void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public JSONObject getPayload() { return this.Payload ;}

}