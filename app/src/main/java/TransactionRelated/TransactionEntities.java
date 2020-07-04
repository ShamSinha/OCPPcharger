package TransactionRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class TransactionEntities {

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

    }

    public static class Transaction{

        public String transactionId ;
        public String chargingState ;
        public int timeSpentCharging ;
        public String stoppedReason ;
        public int remoteStartId ;

    }

    public static class MeterValue{

        public String timestamp ;
        public float value ;
        public String context ;
        public String measurand ;
        public String unit ;
        public int multiplier ;

    }




}
