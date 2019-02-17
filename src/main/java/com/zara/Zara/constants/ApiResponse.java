package com.zara.Zara.constants;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import lombok.Data;

@Data
public class ApiResponse {

    private String responseCode;
    private String responseMessage;
    private Customer customer;
    private Agent agent;
    private Business business;



}
