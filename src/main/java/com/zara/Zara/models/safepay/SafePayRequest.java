package com.zara.Zara.models.safepay;


import com.zara.Zara.models.safepay.shared.Ach;
import com.zara.Zara.models.safepay.shared.BillingDetails;
import com.zara.Zara.models.safepay.shared.Profile;

public class SafePayRequest {

    private String merchantRefNum;
    private float amount;
    Ach AchObject;
    private String customerIp;
    Profile ProfileObject;
    BillingDetails BillingDetailsObject;


    // Getter Methods

    public String getMerchantRefNum() {
        return merchantRefNum;
    }

    public float getAmount() {
        return amount;
    }

    public Ach getAch() {
        return AchObject;
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public Profile getProfile() {
        return ProfileObject;
    }

    public BillingDetails getBillingDetails() {
        return BillingDetailsObject;
    }

    // Setter Methods

    public void setMerchantRefNum(String merchantRefNum) {
        this.merchantRefNum = merchantRefNum;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setAch(Ach achObject) {
        this.AchObject = achObject;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }

    public void setProfile(Profile profileObject) {
        this.ProfileObject = profileObject;
    }

    public void setBillingDetails(BillingDetails billingDetailsObject) {
        this.BillingDetailsObject = billingDetailsObject;
    }
}

