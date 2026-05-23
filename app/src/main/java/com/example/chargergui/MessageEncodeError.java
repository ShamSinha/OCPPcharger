package com.example.chargergui;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class MessageEncodeError implements Encoder.Text<CALLERROR> {
    private static final int CALLERROR_MESSAGE_TYPE_ID = 4;

    @Override
    public String encode(CALLERROR callerror) throws EncodeException {
        if (CALLERROR.getMessageId() == null || callerror.getErrorCode() == null) {
            throw new EncodeException(callerror, "OCPP-J CALLERROR requires a messageId and errorCode");
        }

        JSONArray message = new JSONArray();
        message.put(CALLERROR_MESSAGE_TYPE_ID);
        message.put(CALLERROR.getMessageId());
        message.put(callerror.getErrorCode().name());
        message.put(callerror.getErrorDescription() == null ? "" : callerror.getErrorDescription());
        message.put(callerror.getErrorDetails() == null ? new JSONObject() : callerror.getErrorDetails());
        return message.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
