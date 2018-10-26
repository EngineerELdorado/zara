package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "CUSTOMERS")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "MIDDLE_NAME")
    private String middleName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "NATIONAL_ID")
    private String nationalId;
    @Column(name = "PHONE_NUMBER", unique = true)
    private String phoneNumber;
    @Column(name = "DOB")
    private Date dob;
    @Column(name = "BALANCE")
    private Double balance;
    @Column(name = "PIN")
    private String pin;
    @Column(name = "STATUS")
    private String status;
}
