package UseCasesOCPP;

import android.content.Context;
import android.util.Log;

import com.example.chargergui.CALL;
import com.example.chargergui.CALLERROR;
import com.example.chargergui.CALLRESULT;
import com.example.chargergui.MyClientEndpoint;
import com.example.chargergui.TransactionIdGenerator;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.EncodeException;

import ChargingStationRequest.AuthorizeRequest;
import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.HeartBeatRequest;
import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import TransactionRelated.TransactionEntities;
import TransactionRelated.TransactionEventEnumType;
import TransactionRelated.TransactionEventRepo;


public class SendRequestToCSMS {

    MyClientEndpoint myClientEndpoint ;

    // BootReason default  = PowerUp
    public void sendBootNotificationRequest() throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("BootNotification", BootNotificationRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            send(call);
        }
    }

    //  ConnectorStatus default = Available
    //  Before Sending this request Set EVSE.id and EVSE.connectorId
    public void sendStatusNotificationRequest() throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            StatusNotificationRequest.setTimestamp();
            CALL call = new CALL("StatusNotification", StatusNotificationRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            send(call);
        }
    }

    public void sendHeartBeatRequest() throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("HeartBeat", HeartBeatRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            send(call);
        }
    }

    //Before Sending this make sure IdTokenType is set.
    public void sendAuthorizeRequest() throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("Authorize", AuthorizeRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            send(call);
        }
    }

    //Before Sending this make sure TransactionEvent , TriggerReason, TransactionType.ChargingStatus are set ;
    public void sendTransactionEventRequest(Context context) throws JSONException {
        TransactionEventRepo eventRepo = new TransactionEventRepo(context);
        if(CheckNewCallMessageCanBeSent()) {
            TransactionType.transactionId = TransId(TransactionEventRequest.eventType) ;
            TransactionEventRequest.SetSeqNo(eventRepo.getSeqNo());
            TransactionEventRequest.setTimestamp();
            TransactionEntities.Transaction t = new TransactionEntities.Transaction(TransactionType.transactionId,TransactionType.chargingState.name(),
                    TransactionType.timeSpentCharging,TransactionType.stoppedReason.name(),TransactionType.remoteStartId
                    );

            TransactionEntities.TransactionEventRequest req = new TransactionEntities.TransactionEventRequest(TransactionEventRequest.eventType.name()
                    ,TransactionEventRequest.triggerReason.name(),
                    TransactionEventRequest.timestamp,t) ;

            req.setSeqNo(eventRepo.getSeqNo());
            eventRepo.insertEventReq(req);
            CALL call = new CALL("TransactionEvent", TransactionEventRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            send(call);
        }
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

    private void send(final CALL call) {
        myClientEndpoint = MyClientEndpoint.getInstance() ;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myClientEndpoint.getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG" , "Message Sent" + CALL.getAction());
                    Log.d("TAG", myClientEndpoint.getOpenSession().getId());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR" , "IOException in BasicRemote") ;
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    private boolean CheckNewCallMessageCanBeSent(){
        return CALLRESULT.getMessageId().equals(CALL.getMessageId()) || CALLERROR.getMessageId().equals(CALL.getMessageId());
    }
}
