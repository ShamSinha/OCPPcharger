package UseCasesOCPP;

import DataType.MessageContentType;
import EnumDataType.AuthorizationStatusEnumType;

public class IdTokenInfoType {

    private AuthorizationStatusEnumType status ;
    private String cacheExpiryDateTime ;
    private int chargingPriority ;
    private MessageContentType personalMessage  ;

    public AuthorizationStatusEnumType getStatus() {
        return status;
    }

    public void setStatus(AuthorizationStatusEnumType status) {
        this.status = status;
    }

    public String getCacheExpiryDateTime() {
        return cacheExpiryDateTime;
    }

    public void setCacheExpiryDateTime(String cacheExpiryDateTime) {
        this.cacheExpiryDateTime = cacheExpiryDateTime;
    }

    public int getChargingPriority() {
        return chargingPriority;
    }

    public void setChargingPriority(int chargingPriority) {
        this.chargingPriority = chargingPriority;
    }

    public MessageContentType getPersonalMessage() {
        return personalMessage;
    }

    public void setPersonalMessage(MessageContentType personalMessage) {
        this.personalMessage = personalMessage;
    }
}
