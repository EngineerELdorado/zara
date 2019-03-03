package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "transaction")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PesapayTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "transaction_number")
    public String transactionNumber;
    public String description;
    public String status;
    public Date createdOn;
    // TODO: 18/02/2019 rename this variable to createdByCustomer
    @ManyToOne
    public Customer createdByCustomer;
    public String transactionType;
    @OneToOne
    public Customer receivedByCustomer;
    public BigDecimal amount;
    @ManyToOne
    public Agent createdByAgent;
    @ManyToOne
    public Agent receivedByAgent;
    @ManyToOne
    public Business createdByBusiness;
    @ManyToOne
    public Business receivedByBusiness;
    private String uniqueIdentifier;

}
