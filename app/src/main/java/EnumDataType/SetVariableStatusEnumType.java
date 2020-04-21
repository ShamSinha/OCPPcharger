package EnumDataType;

public enum SetVariableStatusEnumType {
    Accepted , // Variable successfully set.
    Rejected , //Request is rejected.
    InvalidValue , // Value has invalid format for the variable.
    UnknownComponent , //Component is not known.
    UnknownVariable , // Variable is not known.
    NotSupportedAttributeType , // The AttributeType is not supported.
    OutOfRange  , // Value is out of range defined in VariableCharacteristics.
    RebootRequired , // A reboot is required.
}
