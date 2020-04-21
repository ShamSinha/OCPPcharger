package com.example.chargergui;


import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<CALL> {

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(CALL call)  throws EncodeException{


        /*ByteBuffer buffer = null;
        try {
            buffer.putInt(call.getMessageTypeId());
            buffer.put(call.getMessageId().getBytes("UTF-8"));
            buffer.put(call.getAction().getBytes("UTF-8"));
            buffer.put(call.getPayload().toString().getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return buffer;*/

        return MessageFormat.format("{0},{1},{2},{3}", CALL.MessageTypeId, CALL.MessageId, CALL.Action,call.getPayload());
    }


}