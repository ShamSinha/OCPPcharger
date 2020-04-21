package com.example.chargergui;

import org.json.JSONObject;

import java.util.UUID;


public class CALL extends WebsocketMessage{


    public static String Action ;
    private final JSONObject Payload ;
    public static String MessageId ;

    public CALL(String Action,JSONObject Payload){
        CALL.Action = Action ;
        this.Payload = Payload ;
    }

    public static void setMessageId() {
        MessageId = UUID.randomUUID().toString();
    }

    public JSONObject getPayload() { return this.Payload ;}


}