package UseCasesOCPP;

import EnumDataType.RegistrationStatusEnumType;


public class BootNotificationResponse {

    private RegistrationStatusEnumType bootStatus;
    private int bootInterval;

    public RegistrationStatusEnumType getBootStatus() {
        return bootStatus;
    }

    public void setBootStatus(RegistrationStatusEnumType bootStatus) {
        this.bootStatus = bootStatus;
    }

    public int getBootInterval() {
        return bootInterval;
    }

    public void setBootInterval(int bootInterval) {
        this.bootInterval = bootInterval;
    }
}
