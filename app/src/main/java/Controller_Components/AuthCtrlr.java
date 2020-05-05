package Controller_Components;

public class AuthCtrlr {
    private static boolean Enabled = true ; // If this variable reports a value of true, Authorization is enabled
    private static boolean OfflineTxForUnknownIdEnabled; // If this key exists, the Charging Station supports Unknown Offline Authorization.
    private static boolean AuthorizeRemoteStart;//Whether a remote request to start a transaction in the form of
    //RequestStartTransactionRequest message should be authorized beforehand like
    //a local action to start a transaction.
    private static boolean LocalAuthorizeOffline; // Whether the Charging Station, when Offline, will start a transaction for locallyauthorized identifiers.
    private static boolean LocalPreAuthorize; // Whether the Charging Station, when online, will start a transaction for locallyauthorized
    // identifiers without waiting for or requesting an AuthorizeResponse from the CSMS.
    private static String MasterPassGroupId; // IdTokens that have this id as groupId belong to the Master Pass Group.

    public static boolean isEnabled() {
        return Enabled;
    }

    public static void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public static boolean isOfflineTxForUnknownIdEnabled() {
        return OfflineTxForUnknownIdEnabled;
    }

    public static void setOfflineTxForUnknownIdEnabled(boolean offlineTxForUnknownIdEnabled) {
        OfflineTxForUnknownIdEnabled = offlineTxForUnknownIdEnabled;
    }

    public static boolean isAuthorizeRemoteStart() {
        return AuthorizeRemoteStart;
    }

    public static void setAuthorizeRemoteStart(boolean authorizeRemoteStart) {
        AuthorizeRemoteStart = authorizeRemoteStart;
    }

    public static boolean isLocalAuthorizeOffline() {
        return LocalAuthorizeOffline;
    }

    public static void setLocalAuthorizeOffline(boolean localAuthorizeOffline) {
        LocalAuthorizeOffline = localAuthorizeOffline;
    }

    public static boolean isLocalPreAuthorize() {
        return LocalPreAuthorize;
    }

    public static void setLocalPreAuthorize(boolean localPreAuthorize) {
        LocalPreAuthorize = localPreAuthorize;
    }

    public static String getMasterPassGroupId() {
        return MasterPassGroupId;
    }

    public static void setMasterPassGroupId(String masterPassGroupId) {
        MasterPassGroupId = masterPassGroupId;
    }
}

