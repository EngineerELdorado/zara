package com.zara.Zara.models.safepay.shared;

public class Ach {
    private String accountHolderName;
    private String accountType;
    private String accountNumber;
    private String routingNumber;
    private String payMethod;


    // Getter Methods

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public String getPayMethod() {
        return payMethod;
    }

    // Setter Methods

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
