package PhysicalComponents;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChargingStationStates {

    @PrimaryKey
    public String transactionId ;

    public boolean isEVSideCablePluggedIn;
    public boolean isAuthorized ;
    public boolean isDataSigned ;
    public boolean isPowerPathClosed ;
    public boolean isEnergyTransfer ;

    public ChargingStationStates(boolean isEVSideCablePluggedIn, boolean isAuthorized, boolean isDataSigned, boolean isPowerPathClosed, boolean isEnergyTransfer) {
        this.isEVSideCablePluggedIn = isEVSideCablePluggedIn;
        this.isAuthorized = isAuthorized;
        this.isDataSigned = isDataSigned;
        this.isPowerPathClosed = isPowerPathClosed;
        this.isEnergyTransfer = isEnergyTransfer;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean isEVSideCablePluggedIn() {
        return isEVSideCablePluggedIn;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public boolean isDataSigned() {
        return isDataSigned;
    }

    public boolean isPowerPathClosed() {
        return isPowerPathClosed;
    }

    public boolean isEnergyTransfer() {
        return isEnergyTransfer;
    }
}
