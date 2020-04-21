package EnumDataType;

public enum MeasurandEnumType {
    CurrentExport , //Instantaneous current flow from EV
    CurrentImport , //Instantaneous current flow to EV
    CurrentOffered , // Maximum current offered to EV

    EnergyActiveExportRegister ,
    //Numerical value read from the "active electrical energy" (Wh or kWh) register of the (most authoritative)
    //electrical meter measuring energy exported (to the grid).
    EnergyActiveImportRegister ,
    //Numerical value read from the "active electrical energy" (Wh or kWh) register of the (most authoritative)
    //electrical meter measuring energy imported (from the grid supply).

    EnergyActiveExportInterval ,
    // Absolute amount of "active electrical energy" (Wh or kWh) exported (to the grid) during an associated time
    // "interval", specified by a Metervalues ReadingContext, and applicable interval duration configuration values
    //(in seconds) for ClockAlignedDataInterval and TxnMeterValueSampleInterval.

    EnergyActiveImportInterval ,
    //Absolute amount of "active electrical energy" (Wh or kWh) imported (from the grid supply) during an
     //associated time "interval", specified by a Metervalues ReadingContext, and applicable interval duration
    //configuration values (in seconds) for ClockAlignedDataInterval and TxnMeterValueSampleInterval.
    EnergyActiveNet  , // Numerical value read from the “net active electrical energy" (Wh or kWh) register.
    PowerOffered , // Maximum power offered to EV
    SoC ,//State of charge of charging vehicle in percentage
    Voltage // Instantaneous DC or AC RMS supply voltage
}
