package com.zara.Zara.models;

import com.stripe.model.Token;
import lombok.Data;

@Data
public class TransactionRequestBody {

    private String sender;
    private String receiver;
    private String pin;
    private String description;
    private String amount;
    private String apiKey;
    private String stripeToken;
    private String bulkCategoryId;
    private String uniqueIdentifier;
    private String forPaypalEmail;

    private String bankName;
    private String accountName;
    private String swiftCode;
    private String accountNumber;

    @Override
    public String toString() {
        return "TransactionRequestBody{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", pin='" + pin + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", stripeToken='" + stripeToken + '\'' +
                ", bulkCategoryId='" + bulkCategoryId + '\'' +
                ", uniqueIdentifier='" + uniqueIdentifier + '\'' +
                ", forPaypalEmail='" + forPaypalEmail + '\'' +
                '}';
    }
}
