package com.zara.Zara.models;

import com.stripe.model.Token;
import lombok.Data;

@Data
public class ChargeRequest {

    public enum Currency {
        EUR, USD;
    }
    private String description;
    private Double amount; // cents
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}
