package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "transaction_number")
    public String transactionNumber;
    public String description;
    public Date createdOn;
    @ManyToOne
    public AppUser createdBy;
    public String transactionType;
    @OneToOne
    public AppUser receiver;
    public Double amount;

}
