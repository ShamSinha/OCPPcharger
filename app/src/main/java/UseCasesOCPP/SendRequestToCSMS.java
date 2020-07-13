package UseCasesOCPP;

import android.content.Context;

import com.example.chargergui.CALL;
import com.example.chargergui.CALLERROR;
import com.example.chargergui.CALLRESULT;
import com.example.chargergui.TransactionIdGenerator;

import org.json.JSONException;

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

    private TransactionEventRepo eventRepo ;


    // BootReason default  = PowerUp
    public CALL createBootNotificationRequest(Context context) throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("BootNotification", BootNotificationRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            return call;
        }
        return null;
    }

    //  ConnectorStatus default = Available
    //  Before Sending this request Set EVSE.id and EVSE.connectorId
    public CALL createStatusNotificationRequest(Context context) throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            StatusNotificationRequest.setTimestamp();
            CALL call = new CALL("StatusNotification", StatusNotificationRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            return call;
        }
        return null ;
    }

    public CALL createHeartBeatRequest(Context context) throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("HeartBeat", HeartBeatRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            return call;
        }
        return null;
    }

    //Before Sending this make sure IdTokenType is set.
    public CALL createAuthorizeRequest(Context context) throws JSONException {
        if(CheckNewCallMessageCanBeSent()) {
            CALL call = new CALL("Authorize", AuthorizeRequest.payload());
            CALL.setMessageIdIfCallHasToSent();
            return call;
        }
        return null;
    }

    //Before Sending this make sure TransactionEvent , TriggerReason, TransactionType.ChargingStatus are set ;
    public CALL createTransactionEventRequest(Context context) throws JSONException {
        eventRepo = new TransactionEventRepo(context) ;
        if(CheckNewCallMessageCanBeSent()) {
            TransactionType.transactionId = TransId(TransactionEventRequest.eventType) ;
            TransactionEventRequest.SetSeqNo();
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
            return call;
        }
        return null;
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

    private boolean CheckNewCallMessageCanBeSent(){
        return CALLRESULT.getMessageId().equals(CALL.getMessageId()) || CALLERROR.getMessageId().equals(CALL.getMessageId());
    }
}
