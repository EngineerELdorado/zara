package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Entity(name = "businesses")
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String businessName;
    private String phoneNumber;
    @Column(unique = true)
    private String businessNumber;
    private String address;
    private String pin;
    private String role;
//    @JsonIgnore
    private BigDecimal balance;
    private Date createdOn;
    private Long creationDate;
    private String status;
    private String statusDescription;
    private boolean verified;
    @Column(unique = true)
    private String email;
    private String password;
    @Transient
    private String otp;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String profilePic;
    @Column(columnDefinition = "TEXT")
    private String nationalIdPic;
    private String airtelMoneyNumber;
    private String orangeMoneyNumber;
    private String mpesaNumber;
    private String paypalemail;
    private String bankAccountNumber;
    private String callBackUrl;
}
