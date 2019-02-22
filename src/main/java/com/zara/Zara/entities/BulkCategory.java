package com.zara.Zara.entities;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "bulk_categories")
@Data
public class BulkCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    private Business business;

}
