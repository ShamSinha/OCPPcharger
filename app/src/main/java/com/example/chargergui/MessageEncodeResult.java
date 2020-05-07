package com.example.chargergui;

import java.text.MessageFormat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncodeResult implements Encoder.Text<CALLRESULT> {
    @Override
    public String encode(CALLRESULT callresult) throws EncodeException {
        return MessageFormat.format("{0}#{1}#{2}", CALLRESULT.getMessageTypeId(),CALLRESULT.getMessageId(), callresult.getPayload());
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
