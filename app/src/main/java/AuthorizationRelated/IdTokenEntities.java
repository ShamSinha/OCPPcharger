package AuthorizationRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

public class IdTokenEntities {

    public static class IdTokenAndInfo {
        @Embedded
        public IdToken idToken;
        @Relation(parentColumn = "id", entityColumn = "idTokenEntityId")
        public IdTokenInfo idTokenInfo;

        public IdToken getIdToken() {
            return idToken;
        }

        public IdTokenInfo getIdTokenInfo() {
            return idTokenInfo;
        }
    }

    @Entity
    public static class IdToken {

        @PrimaryKey()
        public String transactionId ;

        public String idToken;
        public String type ;
        public String additionalInfo ;

        public IdToken(String idToken, String type, String additionalInfo) {
            this.idToken = idToken;
            this.type = type;
            this.additionalInfo = additionalInfo;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getIdToken() {
            return idToken;
        }

        public String getType() {
            return type;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
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
    @Entity
    public static class IdTokenInfo {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String transactionId ;
        public String status;
        public String cacheExpiryDateTime;
        public int chargingPriority;
        public int evseId;

        @Embedded
        public MessageContent personalMessage;

        public IdTokenInfo(String transactionId, String status, String cacheExpiryDateTime, int chargingPriority, int evseId, MessageContent personalMessage) {
            this.transactionId = transactionId;
            this.status = status;
            this.cacheExpiryDateTime = cacheExpiryDateTime;
            this.chargingPriority = chargingPriority;
            this.evseId = evseId;
            this.personalMessage = personalMessage;
        }

        public String getTransactionId() {
            return transactionId;
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
