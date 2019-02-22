package com.zara.Zara.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "bulk_beneficiaries")
@Data
public class BulkBeneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String phoneNumber;
    @ManyToOne
    private BulkCategory bulkCategory;
    @ManyToOne
    private Business business;
    private BigDecimal amount;
}
