package com.zara.Zara.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "BANKS")
public class Bank {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CODE")
    private String code;
    @Column(name = "WEB_SERVICE_URL")
    private String webServiceUrl;
    @Column(name = "WEB_SERVICE_USERNAME")
    private String webServiceUsername;
    @Column(name = "WEB_SERVICE_PASSWORD")
    private String webServicePassword;
    @Column(name = "WEB_SERVICE_TYPE")
    private String webServiceType;
}
