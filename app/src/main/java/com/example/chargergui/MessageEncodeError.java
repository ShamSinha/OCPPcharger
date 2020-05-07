package com.example.chargergui;

import java.text.MessageFormat;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class MessageEncodeError implements Encoder.Text<CALLERROR> {
    @Override
    public String encode(CALLERROR callerror) throws EncodeException {
        return MessageFormat.format("{0}#{1}#{2}#{3}#{4}", CALLERROR.getMessageTypeId(), CALLERROR.getMessageId(), callerror.getErrorCode(), callerror.getErrorDescription(),callerror.getErrorDetails());
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}