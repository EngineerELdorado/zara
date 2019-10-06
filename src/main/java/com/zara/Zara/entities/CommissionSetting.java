package com.zara.Zara.entities;

import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity(name = "commission_settings")
@Data
public class CommissionSetting {

    private Long id;
    private BigDecimal ceil;
    private BigDecimal top;
    private BigDecimal commission;
    private String setBy;
}
