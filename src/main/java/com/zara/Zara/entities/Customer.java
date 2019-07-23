package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity(name = "customers")
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    @Column(unique = true)
    private String phoneNumber;
    private String email;
    private Date dob1;
    private Long dob2;
    private String gender;
    private String country;
    private String pin;
    private String role;
    private String status;
    private boolean verified;
    private String statusDescription;
    private Date creationDate;
//    @JsonIgnore
    private BigDecimal balance;
    @Transient
    private String otp;
    @Column(columnDefinition = "TEXT")
    private String profilePic;
    @Column(columnDefinition = "TEXT")
    private String nationalIdPic;

}
