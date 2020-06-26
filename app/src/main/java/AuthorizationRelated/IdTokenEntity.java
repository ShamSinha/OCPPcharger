package AuthorizationRelated;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class IdTokenEntity {

    @PrimaryKey(autoGenerate = true)
    public int id ;
    public String idToken;
    public String type ;
    public String additionalInfo ;

    public IdTokenEntity(String idToken, String type, String additionalInfo) {
        this.idToken = idToken;
        this.type = type;
        this.additionalInfo = additionalInfo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
