package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "notifications")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String message;
    private Date date;
    @ManyToOne
    Business business;
    @ManyToOne
    Customer customer;
    @ManyToOne
    Agent agent;
    @ManyToOne
    Admin admin;
    @ManyToOne
    Developer developer;

}
