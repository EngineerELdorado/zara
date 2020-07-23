package com.zara.Zara.entities;

import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@ToString
@SQLDelete(sql = "UPDATE countries SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private String code;

    @Column(length = 3, unique = true, nullable = false)
    private String isoName3;

    @Column(unique = true, updatable = false, nullable = false)
    private String name;

    @Column( nullable = false)
    private String currencyCode;

    @NotNull(message = "Missing required field flagUrl")
    @Column(nullable = false)
    private String flagUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public Country() {
    }

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

    public String getIsoName3() {
        return isoName3;
    }

    public void setIsoName3(String isoName3) {
        this.isoName3 = isoName3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyCode() {
        return currencyCode;
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

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = new Date();
    }
}
