package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "SETTINGS")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "SYSTEM_NAME")
    private String systemName;
    @Column(name = "TAX_PERCENTAGE")
    private String taxPercentage;
    @Column(name = "COMMISSION_PERCENTAGE")
    private Double commissionPercentage;
    @Column(name = "DEFAULT_LANGUAGE")
    private String defaultLanguage;
    @Column(name = "SMS_ENABLED")
    private boolean smsEnabled;


}
