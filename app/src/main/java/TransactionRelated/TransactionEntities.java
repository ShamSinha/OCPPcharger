package TransactionRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;


public class TransactionEntities {

    public static class EventRequestAndResponse{

        @Embedded public TransactionEventRequest transactionEventRequest ;
        @Relation(parentColumn = "SeqNo" ,entityColumn = "ResponseSeqNo") public TransactionEventResponse transactionEventResponse ;
    }

    @Entity
    public static class TransactionEventRequest{

        @PrimaryKey
        public long SeqNo ;

        public String eventType ;
        public String triggerReason ;
        public String timestamp ;

        @Embedded
        public Transaction transaction ;

        public TransactionEventRequest(String eventType, String triggerReason, String timestamp, Transaction transaction) {
            this.eventType = eventType;
            this.triggerReason = triggerReason;
            this.timestamp = timestamp;
            this.transaction = transaction;
        }

        public void setSeqNo(long seqNo) {
            SeqNo = seqNo;
        }

        public long getSeqNo() {
            return SeqNo;
        }

        public String getEventType() {
            return eventType;
        }

        public String getTriggerReason() {
            return triggerReason;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public Transaction getTransaction() {
            return transaction;
        }


    }

    public static class Transaction{

        public String transactionId ;
        public String chargingState ;
        public int timeSpentCharging ;
        public String stoppedReason ;
        public int remoteStartId ;

        public Transaction(String transactionId, String chargingState, int timeSpentCharging, String stoppedReason, int remoteStartId) {
            this.transactionId = transactionId;
            this.chargingState = chargingState;
            this.timeSpentCharging = timeSpentCharging;
            this.stoppedReason = stoppedReason;
            this.remoteStartId = remoteStartId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getChargingState() {
            return chargingState;
        }

        public int getTimeSpentCharging() {
            return timeSpentCharging;
        }

        public String getStoppedReason() {
            return stoppedReason;
        }

        public int getRemoteStartId() {
            return remoteStartId;
        }

    }

    @Entity
    public static class TransactionEventResponse{

        @PrimaryKey(autoGenerate = true)
        public int id ;

        public long ResponseSeqNo ;
        public float totalCost ;
        public int chargingPriority ;

        @Embedded
        public MessageContent updatedPersonalMessage ;

        public TransactionEventResponse(long responseSeqNo, float totalCost, int chargingPriority, MessageContent updatedPersonalMessage) {
            ResponseSeqNo = responseSeqNo;
            this.totalCost = totalCost;
            this.chargingPriority = chargingPriority;
            this.updatedPersonalMessage = updatedPersonalMessage;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public long getResponseSeqNo() {
            return ResponseSeqNo;
        }

        public float getTotalCost() {
            return totalCost;
        }

        public int getChargingPriority() {
            return chargingPriority;
        }

        public MessageContent getUpdatedPersonalMessage() {
            return updatedPersonalMessage;
        }
    }

    public static class MessageContent {

        public String format;
        public String language;
        public String content;

        public MessageContent(String format, String language, String content) {
            this.format = format;
            this.language = language;
            this.content = content;
        }

        public String getFormat() {
            return format;
        }

        public String getLanguage() {
            return language;
        }

        public String getContent() {
            return content;
        }
    }

    @Entity
    public static class SEQNO{

        @PrimaryKey
        public int id ;

        public long SeqNo;

        public SEQNO(int seqNo) {
            SeqNo = seqNo;
        }
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getSeqNo() {
            return SeqNo;
        }
    }
}
