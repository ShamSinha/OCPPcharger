package ChargingRelated;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class ChargeEntity {

    @Entity
    public static class Charging {
        @PrimaryKey
        public String transactionId;

        public float Voltage;
        public float Current;
        public float SOC;

        public Charging(float voltage, float current, float soc) {
            Voltage = voltage;
            Current = current;
            this.SOC = soc;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public float getVoltage() {
            return Voltage;
        }

        public float getCurrent() {
            return Current;
        }

        public float getSOC() {
            return SOC;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }


    @Entity
    public static class Cost{

        @PrimaryKey
        public String transactionId ;

        public float TotalCost ;

        public Cost(float cost) {
            TotalCost = cost;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public float getCost() {
            return TotalCost;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }
}
