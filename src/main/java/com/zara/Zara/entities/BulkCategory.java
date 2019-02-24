package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "bulk_categories")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    private Business business;

    @Override
    public String toString() {
        return "BulkCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", business=" + business +
                '}';
    }
}
