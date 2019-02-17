package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity(name = "agents")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private BigDecimal balance;
    private Date createdOn;
    private String status;
    private String statusDescription;
    private boolean verified;

}
