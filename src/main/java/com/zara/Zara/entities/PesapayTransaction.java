package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "transaction")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PesapayTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "transaction_number")
    public String transactionNumber;
    public String description;
    public String status;
    public Date createdOn;
    @ManyToOne
    public Customer createdCustomer;
    public String transactionType;
    @OneToOne
    public Customer receivedByCustomer;
    public BigDecimal amount;
    @ManyToOne
    public Agent createdByAgent;
    @ManyToOne
    public Agent receivedByAgent;

}
