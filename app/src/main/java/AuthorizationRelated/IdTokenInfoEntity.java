package AuthorizationRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class IdTokenInfoEntity {

    public static class MessageContent {

        public String format;
        public String language;
        public String content;
    }
    @Entity
    public static class IdTokenInfo {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String status;
        public String cacheExpiryDateTime;
        public int chargingPriority;
        @Embedded
        public MessageContent personalMessage;
        public int evseId;

        public IdTokenInfo(String status, String cacheExpiryDateTime, int chargingPriority, MessageContent personalMessage, int evseId) {
            this.status = status;
            this.cacheExpiryDateTime = cacheExpiryDateTime;
            this.chargingPriority = chargingPriority;
            this.personalMessage = personalMessage;
            this.evseId = evseId;
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

  
}