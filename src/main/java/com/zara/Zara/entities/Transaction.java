package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "TRANSACTIONS")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TRANSATION_NUMBER")
    private String transactionNumber;
    private String cbsTransactionNumber;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_ON")
    private Date createdOn;
    @Column(name = "NARRATION")
    private String narration;
    @ManyToOne
    private Customer sender;
    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;
    @OneToOne
    private Customer receiver;
    @Column(name = "AMOUNT")
    private Double amount;

}
