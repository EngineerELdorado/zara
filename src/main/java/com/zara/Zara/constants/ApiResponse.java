package com.zara.Zara.constants;

import com.zara.Zara.entities.*;
import lombok.Data;

import java.util.Collection;

@Data
public class ApiResponse {

    private String responseCode;
    private String responseMessage;
    private Customer customer;
    private Agent agent;
    private Business business;
    private Developer developer;
    private Collection<PesapayTransaction>transactions;


}
