package com.zara.Zara.enums;

public enum TransactionType {

    DEPOSIT, WITHDRAWAL, TRANSFER, PAY_BILL, BUY_GOODS, MOBILE_MONEY_DEPOSIT,
    MOBILE_MONEY_WITHDRAWAL, PAYPAL_DEPOSIT, PAYPAL_WITHDRAWAL;

    public static boolean isThirdPartyTransaction(TransactionType transactionType) {

        return transactionType.name().equals(MOBILE_MONEY_DEPOSIT.name())
                ||
                transactionType.name().equals(MOBILE_MONEY_WITHDRAWAL.name())
                ||
                transactionType.name().equals(PAYPAL_DEPOSIT.name())
                ||
                transactionType.name().equals(PAYPAL_WITHDRAWAL.name());

    }
}
