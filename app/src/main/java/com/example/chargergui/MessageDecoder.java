package com.example.chargergui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import EnumDataType.RPCErrorCodes;

public class MessageDecoder implements Decoder.Text<WebsocketMessage> {

    private static final int CALL_MESSAGE_TYPE_ID = 2;
    private static final int CALLRESULT_MESSAGE_TYPE_ID = 3;
    private static final int CALLERROR_MESSAGE_TYPE_ID = 4;

    @Override
    public WebsocketMessage decode(String s) throws DecodeException {
        try {
            JSONArray message = new JSONArray(s);
            int messagetypeId = message.getInt(0);
            String messageId = message.getString(1);

            if (messagetypeId == CALL_MESSAGE_TYPE_ID) {
                String action = message.getString(2);
                JSONObject payload = message.getJSONObject(3);
                CALL.setMessageTypeId(messagetypeId);
                CALL.setMessageIdIfCallReceived(messageId);
                CALLRESULT.setMessageId(messageId);
                CALLERROR.setMessageId(messageId);
                return new CALL(action, payload);
            }

            if (messagetypeId == CALLRESULT_MESSAGE_TYPE_ID) {
                JSONObject payload = message.getJSONObject(2);
                CALLRESULT.setMessageTypeId(messagetypeId);
                CALLRESULT.setMessageId(messageId);
                return new CALLRESULT(payload);
            }

            if (messagetypeId == CALLERROR_MESSAGE_TYPE_ID) {
                RPCErrorCodes errorcode = RPCErrorCodes.valueOf(message.getString(2));
                String errordescription = message.getString(3);
                JSONObject errordetails = message.getJSONObject(4);
                CALLERROR.setMessageTypeId(messagetypeId);
                CALLERROR.setMessageId(messageId);
                return new CALLERROR(errorcode, errordescription, errordetails);
            }

            throw new DecodeException(s, "Unsupported OCPP-J message type: " + messagetypeId);
        } catch (JSONException | IllegalArgumentException e) {
            throw new DecodeException(s, "Invalid OCPP-J message", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        try {
            JSONArray message = new JSONArray(s);
            int messagetypeId = message.getInt(0);
            if (messagetypeId == CALL_MESSAGE_TYPE_ID) {
                return message.length() == 4
                        && message.get(1) instanceof String
                        && message.get(2) instanceof String
                        && message.get(3) instanceof JSONObject;
            }
            if (messagetypeId == CALLRESULT_MESSAGE_TYPE_ID) {
                return message.length() == 3
                        && message.get(1) instanceof String
                        && message.get(2) instanceof JSONObject;
            }
            if (messagetypeId == CALLERROR_MESSAGE_TYPE_ID) {
                return message.length() == 5
                        && message.get(1) instanceof String
                        && message.get(2) instanceof String
                        && message.get(3) instanceof String
                        && message.get(4) instanceof JSONObject;
            }
        } catch (JSONException | IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    @Override
    public void init(EndpointConfig ec) {

    }

    @Override
    public void destroy() {

    }
}
