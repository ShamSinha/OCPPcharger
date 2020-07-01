package ChargingStationDetails;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import DataType.ModemType;

@Entity
public class ChargingStation {
    
    @PrimaryKey(autoGenerate = true)
    public int id ;
    public String serialNumber ;
    public String model ;
    public String vendorName ;
    public String firmwareVersion ;
    public String modem ;

    public ChargingStation(String serialNumber, String model, String vendorName, String firmwareVersion, String modem) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.vendorName = vendorName;
        this.firmwareVersion = firmwareVersion;
        this.modem = modem;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModel() {
        return model;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getModem() {
        return modem;
    }
}
