package com.example.chargergui;

import org.json.JSONObject;

import java.util.UUID;


public class CALLRESULT extends WebsocketMessage {

    private final JSONObject Payload ;
    public static String MessageId ;


    public CALLRESULT(JSONObject Payload){
        this.Payload = Payload ;
    }
    public JSONObject getPayload() { return this.Payload ;}

}