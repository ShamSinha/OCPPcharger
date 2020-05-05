package Controller_Components;

public class OCPPCommCtrlr {

    private static int HeartbeatInterval ;
    private static int RetryBackOffRepeatTimes;
    private static int RetryBackOffRandomRange ;
    private static int RetryBackOffWaitMinimum ;
    private static int WebSocketPingInterval ;
    private static int DefaultMessageTimeout ;
    private static int ResetRetries ;
    private static boolean UnlockOnEVSideDisconnect ;

    public static int getHeartbeatInterval() {
        return HeartbeatInterval;
    }

    public static void setHeartbeatInterval(int heartbeatInterval) {
        HeartbeatInterval = heartbeatInterval;
    }

    public static int getRetryBackOffRepeatTimes() {
        return RetryBackOffRepeatTimes;
    }

    public static void setRetryBackOffRepeatTimes(int retryBackOffRepeatTimes) {
        RetryBackOffRepeatTimes = retryBackOffRepeatTimes;
    }

    public static int getRetryBackOffRandomRange() {
        return RetryBackOffRandomRange;
    }

    public static void setRetryBackOffRandomRange(int retryBackOffRandomRange) {
        RetryBackOffRandomRange = retryBackOffRandomRange;
    }

    public static int getRetryBackOffWaitMinimum() {
        return RetryBackOffWaitMinimum;
    }

    public static void setRetryBackOffWaitMinimum(int retryBackOffWaitMinimum) {
        RetryBackOffWaitMinimum = retryBackOffWaitMinimum;
    }

    public static int getWebSocketPingInterval() {
        return WebSocketPingInterval;
    }

    public static void setWebSocketPingInterval(int webSocketPingInterval) {
        WebSocketPingInterval = webSocketPingInterval;
    }

    public static int getDefaultMessageTimeout() {
        return DefaultMessageTimeout;
    }

    public static void setDefaultMessageTimeout(int defaultMessageTimeout) {
        DefaultMessageTimeout = defaultMessageTimeout;
    }

    public static int getResetRetries() {
        return ResetRetries;
    }

    public static void setResetRetries(int resetRetries) {
        ResetRetries = resetRetries;
    }

    public static boolean isUnlockOnEVSideDisconnect() {
        return UnlockOnEVSideDisconnect;
    }

    public static void setUnlockOnEVSideDisconnect(boolean unlockOnEVSideDisconnect) {
        UnlockOnEVSideDisconnect = unlockOnEVSideDisconnect;
    }
}
