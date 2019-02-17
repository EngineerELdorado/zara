package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Entity(name = "businesses")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private BigDecimal balance;
    private Date createdOn;
    private String status;
    private String statusDescription;
    private boolean verified;
}
