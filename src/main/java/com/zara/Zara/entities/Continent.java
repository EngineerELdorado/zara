package com.zara.Zara.entities;

import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@SQLDelete(sql = "UPDATE continents SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Table(name = "continents")
public class Continent {

    private static final long serialVersionUID = 2256572546774734322L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "iso_name", length = 3, unique = true)
    private String isoName;

    @NotNull(message = "Missing required field name")
    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    @CreationTimestamp
    @Setter(value = AccessLevel.PRIVATE)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Setter(value = AccessLevel.PRIVATE)
    private Date updatedAt;

    @Column(name = "deleted_at")
    @Setter(value = AccessLevel.PRIVATE)
    private Date deletedAt;

    public boolean isAfrica() {
        return isoName.equalsIgnoreCase("AF");
    }

    public boolean isAntartica() {
        return isoName.equalsIgnoreCase("AN");
    }

    public boolean isAsia() {
        return isoName.equalsIgnoreCase("AS");
    }

    public boolean isEurope() {
        return isoName.equalsIgnoreCase("EU");
    }

    public boolean isNorthAmerica() {
        return isoName.equalsIgnoreCase("NA");
    }

    public boolean isOceania() {
        return isoName.equalsIgnoreCase("OC");
    }

    public boolean isSouthAmerica() {
        return isoName.equalsIgnoreCase("SA");
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = new Date();
    }
}
