package ChargingRelated;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class ChargeEntity {

    @Entity
    public class Charging {
        @PrimaryKey(autoGenerate = true)
        private int id;

        public float Voltage;
        public float Current;
        public float SOC;
        public String transactionId;


        public Charging(float voltage, float current, float soc, String transactionId) {
            Voltage = voltage;
            Current = current;
            this.SOC = soc;
            this.transactionId = transactionId ;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
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
    public class Cost{

        @PrimaryKey(autoGenerate = true)
        private int id;

        public float Cost ;
        public String transactionId ;

        public Cost(float cost, String transactionId) {
            Cost = cost;
            this.transactionId = transactionId;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public float getCost() {
            return Cost;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }
}
