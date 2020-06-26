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

        public float Cost ;

        public Cost(float cost) {
            Cost = cost;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public float getCost() {
            return Cost;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }
}
