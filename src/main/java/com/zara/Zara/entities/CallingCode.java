package com.zara.Zara.entities;


import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@SQLDelete(sql = "UPDATE calling_codes SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Table(name = "calling_codes", uniqueConstraints = {@UniqueConstraint(columnNames = {"country_id", "prefix"})})
public class CallingCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Missing required field country")
    @ManyToOne(targetEntity = Country.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    private Country country;

    @NotNull(message = "Missing required field prefix")
    @Column(name = "prefix", nullable = false)
    private String prefix;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public CallingCode() {
    }

    public CallingCode(Country country, String prefix) {
        this.country = country;
        this.prefix = prefix;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    /*@Override
    public String toString() {
        return "CallingCode{" +
                "id=" + id +
                ", country=" + country +
                ", prefix='" + prefix + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }*/
}
