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

    EnergyActiveNet  , // Numerical value read from the “net active electrical energy" (Wh or kWh) register.
    PowerOffered , // Maximum power offered to EV
    SoC ,//State of charge of charging vehicle in percentage
    Voltage // Instantaneous DC or AC RMS supply voltage
}
