package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "pesapay")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String bankAccountNumber;
    private String bankAccountRoutingNumber;
    private String bankAccountSwiftCode;
    private String bankName;
    private String bankBeneficiaryName;
    private String bankAccountType;

    private String creditCardNumber;
    private String creditCardCvv;
    private String creditExpiryMonth;
    private String creditCardExpiryYear;

    private String paypalEmail;


}
