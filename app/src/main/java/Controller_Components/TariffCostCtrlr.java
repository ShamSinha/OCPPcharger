package Controller_Components;

public class TariffCostCtrlr {

    private static String TariffFallbackMessage ;
    private static String TotalCostFallbackMessage ;
    private static String Currency = "INR" ;

    public static String getTariffFallbackMessage() {
        return TariffFallbackMessage;
    }

    public static void setTariffFallbackMessage(String tariffFallbackMessage) {
        TariffFallbackMessage = tariffFallbackMessage;
    }

    public static String getTotalCostFallbackMessage() {
        return TotalCostFallbackMessage;
    }

    public static void setTotalCostFallbackMessage(String totalCostFallbackMessage) {
        TotalCostFallbackMessage = totalCostFallbackMessage;
    }

    public static String getCurrency() {
        return Currency;
    }

    public static void setCurrency(String currency) {
        Currency = currency;
    }
}
