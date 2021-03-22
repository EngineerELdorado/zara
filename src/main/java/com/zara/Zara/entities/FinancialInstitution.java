package com.zara.Zara.entities;

import lombok.Data;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "financial_institutions")
@SQLDelete(sql = "UPDATE financial_institutions SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Data
public class FinancialInstitution implements Serializable {

    private static final long serialVersionUID = -4307264120073876697L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private String name;
    private String uniqueIdentifier;
    private String logoUrl;

}
