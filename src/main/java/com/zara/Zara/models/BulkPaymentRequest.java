package com.zara.Zara.models;

import lombok.Data;

@Data
public class BulkPaymentRequest {

    private String sender;
    private String categoryId;
    private String pin;
}
