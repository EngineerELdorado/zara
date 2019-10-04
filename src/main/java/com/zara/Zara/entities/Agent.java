package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity(name = "agents")
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String phoneNumber;
    @Column(unique = true)
    private String agentNumber;
    private String address;
    private String pin;
    private String role;
//    @JsonIgnore
    private BigDecimal balance;
    private BigDecimal commission;
    private Long creationDate;
    private Date createdOn;
    private String status;
    private String statusDescription;
    private boolean verified;
    @Transient
    private String otp;
    @Column(columnDefinition = "TEXT")
    private String profilePic;
    @Column(columnDefinition = "TEXT")
    private String nationalIdPic;

}
