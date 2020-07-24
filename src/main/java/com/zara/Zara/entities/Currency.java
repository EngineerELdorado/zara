package com.zara.Zara.entities;

import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "currencies")
@SQLDelete(sql = "UPDATE currencies SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class Currency implements Serializable {

    private static final long serialVersionUID = -5603819446672970788L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Missing required field code")
    @Column(name = "code", unique = true, length = 3, nullable = false)
    private String code;

    @NotNull(message = "Missing required field rate")
    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @NotNull(message = "Missing required field name")
    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public Currency() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = new Date();
    }

}
