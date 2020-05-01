package UseCasesOCPP;

import com.example.chargergui.CALL;
import com.example.chargergui.CALLERROR;
import com.example.chargergui.CALLRESULT;
import com.example.chargergui.MyClientEndpoint;
import com.example.chargergui.TransactionIdGenerator;
import org.json.JSONException;
import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import ChargingStationRequest.AuthorizeRequest;
import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.HeartBeatRequest;
import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import EnumDataType.TransactionEventEnumType;


public class SendRequestToCSMS {
    private MyClientEndpoint myClientEndpoint = new MyClientEndpoint();

    // BootReason default  = PowerUp

    public void sendBootNotificationRequest() throws JSONException, IOException, EncodeException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("BootNotification", BootNotificationRequest.payload());
        CALL.setMessageId();
        myClientEndpoint.SendRequestToServer(call);
    }

    //  ConnectorStatus default = Available
    //  Before Sending this request Set EVSE.id and EVSE.connectorId
    public void sendStatusNotificationRequest() throws IOException, EncodeException, JSONException {
        CheckNewCallMessageCanBeSent();
        StatusNotificationRequest.setTimestamp();
        CALL call = new CALL("StatusNotification",StatusNotificationRequest.payload());
        CALL.setMessageId();
        myClientEndpoint.SendRequestToServer(call);
    }

    public void sendHeartBeatRequest() throws JSONException, IOException, EncodeException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("HeartBeat",HeartBeatRequest.payload());
        CALL.setMessageId();
        myClientEndpoint.SendRequestToServer(call);
    }

    //Before Sending this make sure IdTokenType is set.
    public void sendAuthorizeRequest() throws JSONException, IOException, EncodeException {
        CheckNewCallMessageCanBeSent();
        CALL call = new CALL("Authorize",AuthorizeRequest.payload()) ;
        CALL.setMessageId();
        myClientEndpoint.SendRequestToServer(call);
    }

    //Before Sending this make sure TransactionEvent , TriggerReason, TransactionType.ChargingStatus are set ;
    public void sendTransactionEventRequest() throws JSONException, IOException, EncodeException {
        CheckNewCallMessageCanBeSent();
        TransactionType.transactionId = TransId(TransactionEventRequest.eventType);
        TransactionEventRequest.SetSeqNo();
        TransactionEventRequest.setTimestamp();
        CALL call = new CALL("TransactionEvent", TransactionEventRequest.payload());
        CALL.setMessageId();
        myClientEndpoint.SendRequestToServer(call);
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
