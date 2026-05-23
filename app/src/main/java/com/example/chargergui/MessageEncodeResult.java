package com.example.chargergui;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncodeResult implements Encoder.Text<CALLRESULT> {
    private static final int CALLRESULT_MESSAGE_TYPE_ID = 3;

    @Override
    public String encode(CALLRESULT callresult) throws EncodeException {
        if (CALLRESULT.getMessageId() == null) {
            throw new EncodeException(callresult, "OCPP-J CALLRESULT requires a messageId");
        }

        JSONArray message = new JSONArray();
        message.put(CALLRESULT_MESSAGE_TYPE_ID);
        message.put(CALLRESULT.getMessageId());
        message.put(callresult.getPayload() == null ? new JSONObject() : callresult.getPayload());
        return message.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
