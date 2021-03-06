package com.example.chargergui;

import org.json.JSONObject;

import EnumDataType.RPCErrorCodes;

public class CALLERROR extends WebsocketMessage {

    private final RPCErrorCodes ErrorCode ;
    private final String ErrorDescription ;
    private final JSONObject ErrorDetails ;
    private static String MessageId ;

    public CALLERROR(RPCErrorCodes ErrorCode , String ErrorDescription , JSONObject ErrorDetails){

        this.ErrorCode = ErrorCode ;
        this.ErrorDescription = ErrorDescription ;
        this.ErrorDetails = ErrorDetails ;
    }

    public static String getMessageId() {
        return MessageId;
    }

    public static void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public RPCErrorCodes getErrorCode(){
        return this.ErrorCode ;
    }
    public String getErrorDescription(){
        return this.ErrorDescription ;
    }
    public JSONObject getErrorDetails() {
        return this.ErrorDetails ;}


}
