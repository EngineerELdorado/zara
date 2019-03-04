package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "bulk_categories")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(name = "b_type")
    private String type;
    @ManyToOne
    private Business business;
    @Transient
    private String pin;
    @JsonIgnore
    @OneToMany(
            mappedBy = "bulkCategory",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Collection<BulkBeneficiary>bulkBeneficiaries;

    @Override
    public String toString() {
        return "BulkCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", business=" + business +
                '}';
    }
}
