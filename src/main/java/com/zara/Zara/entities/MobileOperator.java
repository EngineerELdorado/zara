package com.zara.Zara.entities;

import lombok.Data;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "mobile_operators")
@SQLDelete(sql = "UPDATE mobile_operators SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Data
public class MobileOperator implements Serializable {

    private static final long serialVersionUID = -4048385670110875015L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private String name;
    private String uniqueIdentifier;
    private String logoUrl;
    private Date deletedAt;
}
