package com.zara.Zara.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name = "commission_settings")
@Data
public class CommissionSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal ceil;
    private BigDecimal top;
    private BigDecimal commission;
    private String createdBy;
    private String updatedBy;
    private Long createdOn;
    private Long updatedOn;
}
