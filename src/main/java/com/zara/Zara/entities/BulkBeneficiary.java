package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "bulk_beneficiaries")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkBeneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String phoneNumber;
    private String businessNumber;
    @Column(name = "b_type")
    private String type;
    @ManyToOne
    private BulkCategory bulkCategory;
    @ManyToOne
    private Business business;
    private BigDecimal amount;
    @Transient
    private Long categoryId;
    @Transient
    private String businessPin;
}
