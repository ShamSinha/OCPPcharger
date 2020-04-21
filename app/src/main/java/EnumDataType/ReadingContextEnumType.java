package EnumDataType;

public enum ReadingContextEnumType {
    InterruptionBegin , //  Value taken at start of interruption.
    InterruptionEnd , // Value taken when resuming after interruption.
    Other , // Value for any other situations.
    SampleClock  , // Value taken at clock aligned interval.
    SamplePeriodic  , // Value taken as periodic sample relative to start time of transaction.
    TransactionBegin  , //Value taken at start of transaction.
    TransactionEnd , //Value taken at end of transaction.
    Trigger ; // Value taken in response to TriggerMessageRequest.
}
