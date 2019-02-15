package com.zara.Zara.constants;

import com.zara.Zara.entities.Customer;
import lombok.Data;

@Data
public class ApiResponse {

    private String responseCode;
    private String responseMessage;
    private Customer customer;



}
