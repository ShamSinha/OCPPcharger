package AuthorizationRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;


@Entity
public class IdTokenEntity {

    @PrimaryKey
    public String transactionId ;

    public String idToken;
    public String type ;
    public String additionalInfo ;

    public IdTokenEntity(String idToken, String type, String additionalInfo) {
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

  

