package com.example.chargergui;

import java.util.UUID;

public class TransactionIdGenerator {
    public static String transactionId;

    public static void setTransactionId() {
        TransactionIdGenerator.transactionId = UUID.randomUUID().toString();
    }
}
