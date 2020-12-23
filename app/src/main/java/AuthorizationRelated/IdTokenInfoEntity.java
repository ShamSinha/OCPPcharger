package AuthorizationRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class IdTokenInfoEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String status;
    public String cacheExpiryDateTime;
    public int chargingPriority;
    public int evseId;

    @Embedded
    public MessageContent personalMessage;

    public IdTokenInfoEntity(String status, String cacheExpiryDateTime, int chargingPriority, int evseId, MessageContent personalMessage) {

        this.status = status;
        this.cacheExpiryDateTime = cacheExpiryDateTime;
        this.chargingPriority = chargingPriority;
        this.evseId = evseId;
        this.personalMessage = personalMessage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCacheExpiryDateTime() {
        return cacheExpiryDateTime;
    }

    public int getChargingPriority() {
        return chargingPriority;
    }

    public MessageContent getPersonalMessage() {
        return personalMessage;
    }

    public int getEvseId() {
        return evseId;
    }
}
