package EnumDataType;

public enum MessageTriggerEnumType {
    BootNotification, // To trigger BootNotification.
    LogStatusNotification, // To trigger LogStatusNotification.
    FirmwareStatusNotification , //To trigger FirmwareStatusNotification.
    Heartbeat , //To trigger Heartbeat.
    MeterValues , //To trigger MeterValues.
    SignChargingStationCertificate , // To trigger a SignCertificate with typeOfCertificate: ChargingStationCertificate.
    SignV2GCertificate , //To trigger a SignCertificate with typeOfCertificate: V2GCertificate
    StatusNotification , // To trigger StatusNotification.
    TransactionEvent, // To trigger TransactionEvent.
    SignCombinedCertificate , // To trigger a SignCertificate with typeOfCertificate: ChargingStationCertificate AND V2GCertificate
    PublishFirmwareStatusNotification , // To trigger PublishFirmwareStatusNotification
}
