package Controller_Components;

public class AuthCtrlr {
    public static boolean Enabled = true ; // If this variable reports a value of true, Authorization is enabled
    public static boolean OfflineTxForUnknownIdEnabled; // If this key exists, the Charging Station supports Unknown Offline Authorization.
    public static boolean AuthorizeRemoteStart;//Whether a remote request to start a transaction in the form of
    //RequestStartTransactionRequest message should be authorized beforehand like
    //a local action to start a transaction.
    public static boolean LocalAuthorizeOffline; // Whether the Charging Station, when Offline, will start a transaction for locallyauthorized identifiers.
    public static boolean LocalPreAuthorize; // Whether the Charging Station, when online, will start a transaction for locallyauthorized
    // identifiers without waiting for or requesting an AuthorizeResponse from the CSMS.
    public static String MasterPassGroupId; // IdTokens that have this id as groupId belong to the Master Pass Group.

}

