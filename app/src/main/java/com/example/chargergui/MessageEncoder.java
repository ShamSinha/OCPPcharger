package com.example.chargergui;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<CALL> {

    private static final int CALL_MESSAGE_TYPE_ID = 2;

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(CALL call)  throws EncodeException{


        if (CALL.getMessageId() == null || CALL.getAction() == null) {
            throw new EncodeException(call, "OCPP-J CALL requires a messageId and action");
        }

        JSONArray message = new JSONArray();
        message.put(CALL_MESSAGE_TYPE_ID);
        message.put(CALL.getMessageId());
        message.put(CALL.getAction());
        message.put(call.getPayload() == null ? new JSONObject() : call.getPayload());
        return message.toString();
    }


}
