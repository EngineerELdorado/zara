package com.zara.Zara.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequestBody {

    private String sender;
    private String receiver;
    private String agentNumber;
    private String pin;
    private String description;
    private String amount;

    @Override
    public String toString() {
        return "TransactionRequestBody{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", agentNumber='" + agentNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
