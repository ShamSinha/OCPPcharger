package EnumDataType;

public enum EventTriggerEnumType {
    Alerting , // Monitored variable has passed an Alert or Critical threshold
    Delta , // Delta Monitored Variable value has changed by more than specified amount
    Periodic ; // Periodic Monitored Variable has been sampled for reporting at the specified interval
}
