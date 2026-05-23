package com.example.chargergui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class OcppActionRegistry {
    private static final Set<String> KNOWN_ACTIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "AFRRSignal",
            "AdjustPeriodicEventStream",
            "Authorize",
            "BatterySwap",
            "BootNotification",
            "CancelReservation",
            "CertificateSigned",
            "ChangeAvailability",
            "ChangeTransactionTariff",
            "ClearCache",
            "ClearChargingProfile",
            "ClearDERControl",
            "ClearDisplayMessage",
            "ClearTariffs",
            "ClearVariableMonitoring",
            "ClearedChargingLimit",
            "ClosePeriodicEventStream",
            "CostUpdated",
            "CustomerInformation",
            "DataTransfer",
            "DeleteCertificate",
            "FirmwareStatusNotification",
            "Get15118EVCertificate",
            "GetBaseReport",
            "GetCertificateChainStatus",
            "GetCertificateStatus",
            "GetChargingProfiles",
            "GetCompositeSchedule",
            "GetDERControl",
            "GetDisplayMessages",
            "GetInstalledCertificateIds",
            "GetLocalListVersion",
            "GetLog",
            "GetMonitoringReport",
            "GetPeriodicEventStream",
            "GetReport",
            "GetTariffs",
            "GetTransactionStatus",
            "GetVariables",
            "Heartbeat",
            "InstallCertificate",
            "LogStatusNotification",
            "MeterValues",
            "NotifyAllowedEnergyTransfer",
            "NotifyChargingLimit",
            "NotifyCustomerInformation",
            "NotifyDERAlarm",
            "NotifyDERStartStop",
            "NotifyDisplayMessages",
            "NotifyEVChargingNeeds",
            "NotifyEVChargingSchedule",
            "NotifyEvent",
            "NotifyMonitoringReport",
            "NotifyPeriodicEventStream",
            "NotifyPriorityCharging",
            "NotifyReport",
            "NotifySettlement",
            "NotifyWebPaymentStarted",
            "OpenPeriodicEventStream",
            "PublishFirmware",
            "PublishFirmwareStatusNotification",
            "PullDynamicScheduleUpdate",
            "ReportChargingProfiles",
            "ReportDERControl",
            "RequestBatterySwap",
            "RequestStartTransaction",
            "RequestStopTransaction",
            "ReservationStatusUpdate",
            "ReserveNow",
            "Reset",
            "SecurityEventNotification",
            "SendLocalList",
            "SetChargingProfile",
            "SetDERControl",
            "SetDefaultTariff",
            "SetDisplayMessage",
            "SetMonitoringBase",
            "SetMonitoringLevel",
            "SetNetworkProfile",
            "SetVariableMonitoring",
            "SetVariables",
            "SignCertificate",
            "StatusNotification",
            "TransactionEvent",
            "TriggerMessage",
            "UnlockConnector",
            "UnpublishFirmware",
            "UpdateDynamicSchedule",
            "UpdateFirmware",
            "UsePriorityCharging",
            "VatNumberValidation"
    )));

    private static final Set<String> IMPLEMENTED_SERVER_REQUESTS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "CostUpdated",
            "GetDisplayMessages",
            "GetVariables",
            "Reset",
            "SetDisplayMessage",
            "SetDisplayMessages",
            "SetNetworkProfile",
            "SetVariables"
    )));

    private static final Set<String> IMPLEMENTED_CHARGER_REQUESTS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "Authorize",
            "BootNotification",
            "Heartbeat",
            "StatusNotification",
            "TransactionEvent"
    )));

    private OcppActionRegistry() {
    }

    public static boolean isKnownAction(String action) {
        return KNOWN_ACTIONS.contains(action);
    }

    public static boolean isImplementedServerRequest(String action) {
        return IMPLEMENTED_SERVER_REQUESTS.contains(action);
    }

    public static boolean isImplementedChargerRequest(String action) {
        return IMPLEMENTED_CHARGER_REQUESTS.contains(action);
    }
}
