package UseCasesOCPP;

import com.example.chargergui.CALL;
import com.example.chargergui.CALLERROR;
import com.example.chargergui.CALLRESULT;
import com.example.chargergui.MyClientEndpoint;
import com.example.chargergui.TransactionIdGenerator;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.AuthorizeRequest;
import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.HeartBeatRequest;
import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import EnumDataType.TransactionEventEnumType;


public class SendRequestToCSMS {

    private static SendRequestToCSMS instance = new SendRequestToCSMS(); // Eagerly Loading of single ton instance

    private SendRequestToCSMS(){
        // private to prevent anyone else from instantiating
    }

    public static SendRequestToCSMS getInstance(){
        return instance;
    }

        // BootReason default  = PowerUp
    public CALL createBootNotificationRequest() throws JSONException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("BootNotification", BootNotificationRequest.payload());
        CALL.setMessageId();
        return call ;
    }

    //  ConnectorStatus default = Available
    //  Before Sending this request Set EVSE.id and EVSE.connectorId
    public CALL createStatusNotificationRequest() throws JSONException {
        CheckNewCallMessageCanBeSent();
        StatusNotificationRequest.setTimestamp();
        CALL call = new CALL("StatusNotification",StatusNotificationRequest.payload());
        CALL.setMessageId();
        return call ;
    }

    public CALL createHeartBeatRequest() throws JSONException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("HeartBeat",HeartBeatRequest.payload());
        CALL.setMessageId();
        return call ;
    }

    //Before Sending this make sure IdTokenType is set.
    public CALL createAuthorizeRequest() throws JSONException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("Authorize",AuthorizeRequest.payload()) ;
        CALL.setMessageId();
        return call ;

    }

    //Before Sending this make sure TransactionEvent , TriggerReason, TransactionType.ChargingStatus are set ;
    public CALL createTransactionEventRequest() throws JSONException {
        CheckNewCallMessageCanBeSent();
        TransactionType.transactionId = TransId(TransactionEventRequest.eventType);
        TransactionEventRequest.SetSeqNo();
        TransactionEventRequest.setTimestamp();
        CALL call = new CALL("TransactionEvent", TransactionEventRequest.payload());
        CALL.setMessageId();
        return call;
    }


    private static String TransId(TransactionEventEnumType transactionEventEnumType){
        if(transactionEventEnumType == TransactionEventEnumType.Started){
            TransactionIdGenerator.setTransactionId();
            return TransactionIdGenerator.transactionId ;
        }
        if(transactionEventEnumType == TransactionEventEnumType.Updated){
            return TransactionIdGenerator.transactionId ;
        }
        if(transactionEventEnumType == TransactionEventEnumType.Ended){
            return TransactionIdGenerator.transactionId ;
        }

        return null;
    }

    private void CheckNewCallMessageCanBeSent(){
        while(true){
            if(CALLRESULT.MessageId.equals(CALL.MessageId) || CALLERROR.MessageId.equals(CALL.MessageId)){
                break;
            }
        }
    }

}
