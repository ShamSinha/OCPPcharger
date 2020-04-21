package com.example.chargergui;

import java.util.StringTokenizer;
import org.json.JSONException;
import org.json.JSONObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import EnumDataType.RPCErrorCodes;


public class MessageDecoder implements Decoder.Text<WebsocketMessage> {

    @Override
    public WebsocketMessage decode(String s) throws DecodeException {

        StringTokenizer st = new StringTokenizer(s,",");
        int messagetypeId = Integer.parseInt(st.nextToken());
        if(messagetypeId == 2){
            String messageId = st.nextToken();
            String action = st.nextToken();
            JSONObject payload = null;
            try {
                payload = new JSONObject(st.nextToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CALL.MessageTypeId = messagetypeId ;
            CALL.MessageId = messageId ;
            return new CALL(action, payload) ;
        }

        if(messagetypeId == 3){
            String messageId = st.nextToken();
            JSONObject payload = null;
            try {
                payload = new JSONObject(st.nextToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CALLRESULT.MessageTypeId = messagetypeId ;
            CALLRESULT.MessageId = messageId ;
            return new CALLRESULT(payload) ;
        }
        if(messagetypeId == 4){
            String messageId = st.nextToken();
            RPCErrorCodes errorcode = RPCErrorCodes.valueOf(st.nextToken());
            String errordescription = st.nextToken();
            JSONObject errordetails  = null;
            try {
                errordetails = new JSONObject(st.nextToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CALLERROR.MessageTypeId = messagetypeId ;
            CALLERROR.MessageId = messageId ;

            return new CALLERROR(errorcode,errordescription, errordetails) ;
        }


        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean willDecode(String s) {
        StringTokenizer st = new StringTokenizer(s,",");
        int messagetypeId = Integer.parseInt(st.nextToken());
        if(messagetypeId == 2 || messagetypeId == 3 || messagetypeId == 4){
            return true ;
        }
        else
            return false ;
    }

    @Override
    public void init(EndpointConfig ec) {

    }

    @Override
    public void destroy() {

    }

}
