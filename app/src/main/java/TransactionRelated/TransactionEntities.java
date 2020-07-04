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
        public int SeqNo ;

        public String eventType ;
        public Boolean offline ;
        public float cableMaxCurrent ;
        public String triggerReason ;
        public String timestamp ;

        @Embedded
        public Transaction transaction ;

        @Embedded
        public MeterValue meterValue ;

        public TransactionEventRequest(String eventType, Boolean offline, float cableMaxCurrent, String triggerReason, String timestamp, Transaction transaction, MeterValue meterValue) {
            this.eventType = eventType;
            this.offline = offline;
            this.cableMaxCurrent = cableMaxCurrent;
            this.triggerReason = triggerReason;
            this.timestamp = timestamp;
            this.transaction = transaction;
            this.meterValue = meterValue;
        }

        public void setSeqNo(int seqNo) {
            SeqNo = seqNo;
        }

        public int getSeqNo() {
            return SeqNo;
        }

        public String getEventType() {
            return eventType;
        }

        public Boolean getOffline() {
            return offline;
        }

        public float getCableMaxCurrent() {
            return cableMaxCurrent;
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

        public MeterValue getMeterValue() {
            return meterValue;
        }
    }

    public static class Transaction{

        public String transactionId ;
        public String chargingState ;
        public int timeSpentCharging ;
        public String stoppedReason ;
        public int remoteStartId ;

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

    public static class MeterValue {

        public String timestamp;
        public float value;
        public String context;
        public String measurand;
        public String unit;
        public int multiplier;

        public String getTimestamp() {
            return timestamp;
        }

        public float getValue() {
            return value;
        }

        public String getContext() {
            return context;
        }

        public String getMeasurand() {
            return measurand;
        }

        public String getUnit() {
            return unit;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    @Entity
    public static class TransactionEventResponse{

        @PrimaryKey(autoGenerate = true)
        public int id ;

        public int ResponseSeqNo ;
        public float totalCost ;
        public int chargingPriority ;

        @Embedded
        public MessageContent updatedPersonalMessage ;

        public TransactionEventResponse(int responseSeqNo, float totalCost, int chargingPriority, MessageContent updatedPersonalMessage) {
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

        public int getResponseSeqNo() {
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





}
